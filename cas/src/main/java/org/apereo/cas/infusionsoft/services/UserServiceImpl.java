package org.apereo.cas.infusionsoft.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.api.UserAccountDTO;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.*;
import org.apereo.cas.infusionsoft.domain.*;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apereo.cas.infusionsoft.web.ValidationUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.util.*;

@Transactional(transactionManager = "transactionManager")
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private AppHelper appHelper;
    private AuthorityDAO authorityDAO;
    private LoginAttemptDAO loginAttemptDAO;
    private MailService mailService;
    private PasswordService passwordService;
    private PendingUserAccountDAO pendingUserAccountDAO;
    private UserDAO userDAO;
    private UserAccountDAO userAccountDAO;
    private UserIdentityDAO userIdentityDAO;
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    public UserServiceImpl(AppHelper appHelper, AuthorityDAO authorityDAO, LoginAttemptDAO loginAttemptDAO, MailService mailService, PasswordService passwordService, PendingUserAccountDAO pendingUserAccountDAO, UserDAO userDAO, UserAccountDAO userAccountDAO, UserIdentityDAO userIdentityDAO, InfusionsoftConfigurationProperties infusionsoftConfigurationProperties) {
        this.appHelper = appHelper;
        this.authorityDAO = authorityDAO;
        this.loginAttemptDAO = loginAttemptDAO;
        this.mailService = mailService;
        this.passwordService = passwordService;
        this.pendingUserAccountDAO = pendingUserAccountDAO;
        this.userDAO = userDAO;
        this.userAccountDAO = userAccountDAO;
        this.userIdentityDAO = userIdentityDAO;
        this.infusionsoftConfigurationProperties = infusionsoftConfigurationProperties;
    }

    @Override
    public Authority findAuthorityByName(String authorityName) {
        return authorityDAO.findByAuthority(authorityName);
    }

    @Override
    public User saveUser(User user) throws InfusionsoftValidationException {
        boolean beingAdded = (user.getId() == null);
        if (beingAdded) {
            user.getAuthorities().add(findAuthorityByName("ROLE_CAS_USER"));
        }

        // Check if there is already a different user with the new username
        // NOTE: the Hibernate constraints will already force this, but I couldn't find a way to customize the exception message
        if (isDuplicateUsername(user)) {
            throw new InfusionsoftValidationException("user.error.email.inUse");
        }

        // NOTE: these are enforced by the annotation "@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)" but this way the tags are just removed instead of throwing an error
        user.setFirstName(ValidationUtils.removeAllHtmlTags(user.getFirstName()));
        user.setLastName(ValidationUtils.removeAllHtmlTags(user.getLastName()));

        //TODO: if this user is currently logged in then change the object in the security context

        return userDAO.save(user);
    }

    @Override
    public User createUser(User user, String plainTextPassword) throws InfusionsoftValidationException {
        User savedUser = saveUser(user);
        passwordService.setPasswordForUser(savedUser, plainTextPassword);
        return savedUser;
    }

    @Override
    @Deprecated
    public User loadUser(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public User loadUser(Long id) {
        return userDAO.findOne(id);
    }

    @Override
    public String resetPassword(User user) {
        user = updatePasswordRecoveryCode(user.getId());
        String recoveryCode = user.getPasswordRecoveryCode();

        log.info("password recovery code " + recoveryCode + " created for user " + user);

        mailService.sendPasswordResetEmail(user);

        return recoveryCode;
    }

    /**
     * Attempts to find a user by their recovery code.
     */
    @Override
    public User findUserByRecoveryCode(String recoveryCode) {
        User retVal = null;
        User user = userDAO.findByPasswordRecoveryCode(recoveryCode);

        // TODO: use UTC date here
        if (user != null && user.getPasswordRecoveryCodeCreatedTime().plusMinutes(30).isAfter(new DateTime())) {
            retVal = user;
        }

        return retVal;
    }

    /**
     * Updates the password recovery code for a user.
     */
    @Override
    public synchronized User updatePasswordRecoveryCode(long userId) {
        String recoveryCode = generateRecoveryCode();
        // Keep generating new codes until we find one that's not already in use
        while (findUserByRecoveryCode(recoveryCode) != null) {
            recoveryCode = generateRecoveryCode();
        }

        // Load the user, to avoid opening the door to updates on more than just the password recovery code
        User user = loadUser(userId);
        user.setPasswordRecoveryCode(recoveryCode);
        // TODO: use UTC date here
        user.setPasswordRecoveryCodeCreatedTime(new DateTime());

        return userDAO.save(user);
    }

    @Override
    public synchronized User clearPasswordRecoveryCode(long userId) {
        // Load the user, to avoid opening the door to updates on more than just the password recovery code
        User user = loadUser(userId);
        if (StringUtils.isBlank(user.getPasswordRecoveryCode())) {
            return user;
        } else {
            user.setPasswordRecoveryCode(null);
            user.setPasswordRecoveryCodeCreatedTime(null);

            log.info("Cleared password recovery code for user " + user);
            return userDAO.save(user);
        }
    }

    /**
     * Creates a unique, random password recovery code.
     */
    private String generateRecoveryCode() {
        return RandomStringUtils.randomAlphabetic(12).toUpperCase();
    }

    /**
     * Finds a user account by for a user.
     */
    @Override
    @Deprecated
    public UserAccount findUserAccount(User user, String appName, AppType appType) {
        return userAccountDAO.findByUserAndAppNameAndAppType(user, appName, appType);
    }

    /**
     * Returns a user's accounts, sorted by type and name for consistency when displaying lists of connected accounts.
     */
    @Override
    @Deprecated
    public List<UserAccount> findSortedUserAccountsByAppType(User user, AppType appType) {
        return userAccountDAO.findByUserAndAppTypeAndDisabledOrderByAppNameAsc(user, appType, false);
    }

    /**
     * Tries to associate a user with a pending registration. If successful, this
     * will return the newly associated user account. If there's already a disabled user account matching the
     * registration (unlikely), re-enable it instead of creating a duplicate.
     */
    @Override
    public UserAccount associatePendingAccountToUser(User user, String registrationCode) {
        PendingUserAccount pendingAccount = findPendingUserAccount(registrationCode);
        ensureAccountIsNotLinkedToDifferentUser(pendingAccount.getAppName(), pendingAccount.getAppType(), pendingAccount.getAppUsername(), user);
        UserAccount account = findUserAccount(user, pendingAccount.getAppName(), pendingAccount.getAppType());

        try {
            if (account == null) {
                account = new UserAccount();

                account.setUser(user);
                account.setAppName(pendingAccount.getAppName());
                account.setAppType(pendingAccount.getAppType());
                account.setAppUsername(pendingAccount.getAppUsername());

                user.getAccounts().add(account);

                userAccountDAO.save(account);
                userDAO.save(user);
            } else {
                account.setAppUsername(pendingAccount.getAppUsername());
                account.setDisabled(false);

                userAccountDAO.save(account);
            }

            log.info("associated user " + user + " to " + account.getAppName() + "/" + account.getAppType());

            pendingUserAccountDAO.delete(pendingAccount);
        } catch (Exception e) {
            throw new IllegalStateException("failed to associate user " + user + " to registration code " + registrationCode, e);
        }

        return account;
    }

    private void ensureAccountIsNotLinkedToDifferentUser(String appName, AppType appType, String appUsername, User user) {
        List<UserAccount> accounts = userAccountDAO.findByAppNameAndAppTypeAndAppUsernameAndUserNot(appName, appType, appUsername, user);

        if (accounts != null && !accounts.isEmpty()) {
            List<String> usernames = new ArrayList<>();
            for (UserAccount account : accounts) {
                usernames.add(account.getUser().getUsername());
            }
            throw new IllegalStateException("Account " + appUsername + " on " + appName + "/" + appType + " could not be linked to " + user + " since it is already linked to a different Infusionsoft ID (" + StringUtils.join(usernames, ", ") + ")");
        }
    }

    /**
     * Finds a pending user account by its unique registration code.
     */
    @Override
    public PendingUserAccount findPendingUserAccount(String registrationCode) {
        return pendingUserAccountDAO.findByRegistrationCode(registrationCode);
    }

    /**
     * Finds an enabled user by username.
     */
    @Override
    @Deprecated
    public User findEnabledUser(String username) {
        return userDAO.findByUsernameAndEnabled(username, true);
    }

    @Override
    public void cleanupLoginAttempts() {
        final long loginAttemptMaxAge = infusionsoftConfigurationProperties.getLoginAttemptMaxAge();
        log.info("cleaning up login attempts older than " + loginAttemptMaxAge + " ms");

        Date date = new Date(System.currentTimeMillis() - loginAttemptMaxAge);
        List<LoginAttempt> attempts = loginAttemptDAO.findByDateAttemptedLessThan(date);

        log.info("deleting " + attempts.size() + " login attempts that occurred before " + date);

        loginAttemptDAO.delete(attempts);
    }

    @Override
    @Deprecated
    public List<UserAccount> findActiveUserAccounts(User user) {
        return userAccountDAO.findByUserAndDisabled(user, false);
    }

    @Override
    @Deprecated
    public boolean isDuplicateUsername(User user) {
        if (user == null) {
            return false;
        } else if (user.getId() == null) {
            return userDAO.findByUsername(user.getUsername()) != null;
        } else {
            return userDAO.findByUsernameAndIdNot(user.getUsername(), user.getId()) != null;
        }
    }

    @Override
    public Map<String, Object> createAttributeMapForUser(@NotNull User user) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("id", user.getId());
        attributes.put("displayName", user.getFirstName() + " " + user.getLastName());
        attributes.put("firstName", user.getFirstName());
        attributes.put("lastName", user.getLastName());
        attributes.put("email", user.getUsername());

        // We use a query instead of user.getAccounts() so that we only include enabled accounts
        List<UserAccount> accounts = findActiveUserAccounts(user);
        attributes.put("accounts", getAccountsJSON(accounts));
        attributes.put("authorities", user.getAuthorities());

        return attributes;
    }

    @Override
    public User findUserByExternalId(String externalId) {
        User retVal = null;
        UserIdentity userIdentity = userIdentityDAO.findByExternalId(externalId);

        if (userIdentity != null) {
            retVal = userIdentity.getUser();
        }

        return retVal;
    }

    @Override
    public UserIdentity findUserIdentityByExternalId(String externalId) {
        return userIdentityDAO.findByExternalId(externalId);
    }

    @Override
    public UserIdentity saveUserIdentity(UserIdentity userIdentity) throws InfusionsoftValidationException {
        return userIdentityDAO.save(userIdentity);
    }

    /**
     * *************************************************************************************************
     * * * WARNING * * *
     * If the format/content of this JSON ever changes in a way that affects parsing on the receiving end,
     * the TICKETGRANTINGTICKET table needs to be completely cleared, since the old tickets stored there
     * will still have the old format
     * **************************************************************************************************
     */
    private String getAccountsJSON(List<UserAccount> accounts) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String json = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserAccountDTO[] userAccounts = UserAccountDTO.convertFromCollection(accounts, appHelper);
            objectMapper.writeValue(outputStream, userAccounts);
            json = outputStream.toString("UTF-8");
        } catch (Exception e) {
            log.error("Error while serializing accounts to JSON", e);
        }

        return json;
    }

}