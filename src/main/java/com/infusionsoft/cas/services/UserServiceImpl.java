package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.*;
import com.infusionsoft.cas.domain.*;
import com.infusionsoft.cas.exceptions.AccountException;
import com.infusionsoft.cas.exceptions.DuplicateAccountException;
import com.infusionsoft.cas.exceptions.InfusionsoftValidationException;
import com.infusionsoft.cas.oauth.exceptions.OAuthException;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.web.ValidationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private AuthorityDAO authorityDAO;

    @Autowired
    private LoginAttemptDAO loginAttemptDAO;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private PendingUserAccountDAO pendingUserAccountDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserAccountDAO userAccountDAO;

    @Autowired
    private OAuthService oauthService;

    @Value("${infusionsoft.cas.garbageman.loginattemptmaxage}")
    private long loginAttemptMaxAge = 86400000; // default to 1 day

    @Override
    public List<Authority> findAllAuthorities() {
        List<Authority> authorities = new LinkedList<Authority>();
        CollectionUtils.addAll(authorities, authorityDAO.findAll().iterator());

        Collections.sort(authorities, new Comparator<Authority>() {
            @Override
            public int compare(Authority o, Authority o2) {
                return o.getAuthority().compareTo(o2.getAuthority());
            }
        });

        return authorities;
    }

    @Override
    public Authority findAuthorityByName(String authorityName) {
        return authorityDAO.findByAuthority(authorityName);
    }

    @Override
    public Page<User> findByUsernameLike(String username, Pageable pageable) {
        if (StringUtils.isEmpty(username)) {
            username = "%";
        } else if (StringUtils.contains(username, "*") || StringUtils.contains(username, "%")) {
            username = username.replace("*", "%");
        } else {
            username = "%" + username + "%";
        }

        return userDAO.findByUsernameLike(username, pageable);
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

        User savedUser = userDAO.save(user);

        //TODO: if this user is currently logged in then change the object in the security context

        return savedUser;
    }

    @Override
    public User createUser(User user, String plainTextPassword) throws InfusionsoftValidationException {
        User savedUser = saveUser(user);
        passwordService.setPasswordForUser(savedUser, plainTextPassword);
        return savedUser;
    }

    @Override
    public UserAccount saveUserAccount(UserAccount userAccount) {
        // NOTE: this is enforced by the annotation "@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)" but this way the tags are just removed instead of throwing an error
        userAccount.setAlias(ValidationUtils.removeAllHtmlTags(userAccount.getAlias()));
        return userAccountDAO.save(userAccount);
    }

    @Override
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
     * Finds a user account by id, but only if it belongs to a given user.
     */
    @Override
    public UserAccount findUserAccount(User user, Long accountId) {
        return userAccountDAO.findByUserAndId(user, accountId);
    }

    /**
     * Finds a user account by for a user.
     */
    @Override
    public UserAccount findUserAccount(User user, String appName, AppType appType) {
        return userAccountDAO.findByUserAndAppNameAndAppType(user, appName, appType);
    }

    /**
     * Returns a user's accounts, sorted by type and name for consistency.
     */
    @Override
    public List<UserAccount> findSortedUserAccounts(User user) {
        List<UserAccount> accounts = new ArrayList<UserAccount>();

        accounts.addAll(userAccountDAO.findByUserAndAppTypeAndDisabledOrderByAppNameAsc(user, AppType.CRM, false));
        accounts.addAll(userAccountDAO.findByUserAndAppTypeAndDisabledOrderByAppNameAsc(user, AppType.COMMUNITY, false));
        accounts.addAll(userAccountDAO.findByUserAndAppTypeAndDisabledOrderByAppNameAsc(user, AppType.CUSTOMERHUB, false));

        return accounts;
    }

    /**
     * Returns a user's accounts, sorted by type and name for consistency when displaying lists of connected accounts.
     */
    @Override
    public List<UserAccount> findSortedUserAccountsByAppType(User user, AppType appType) {
        return userAccountDAO.findByUserAndAppTypeAndDisabledOrderByAppNameAsc(user, appType, false);
    }

    /**
     * Creates a pending user account for someone, so they can come back and complete registration later.
     * If one is already pending, regurgitate the same code.
     */
    @Override
    public PendingUserAccount createPendingUserAccount(AppType appType, String appName, String appUsername, String
            firstName, String lastName, String email, boolean passwordVerificationRequired) {
        PendingUserAccount pendingUserAccount = pendingUserAccountDAO.findByAppTypeAndAppNameAndAppUsername(appType, appName, appUsername);

        if (pendingUserAccount != null) {
            pendingUserAccount.setFirstName(firstName);
            pendingUserAccount.setLastName(lastName);
            pendingUserAccount.setEmail(email);
            pendingUserAccount.setPasswordVerificationRequired(passwordVerificationRequired);

            pendingUserAccountDAO.save(pendingUserAccount);
        } else {
            pendingUserAccount = new PendingUserAccount();

            pendingUserAccount.setAppName(appName);
            pendingUserAccount.setAppType(appType);
            pendingUserAccount.setAppUsername(appUsername);
            pendingUserAccount.setRegistrationCode(appName + "-" + RandomStringUtils.random(16, true, true));
            pendingUserAccount.setFirstName(firstName);
            pendingUserAccount.setLastName(lastName);
            pendingUserAccount.setEmail(email);
            pendingUserAccount.setPasswordVerificationRequired(passwordVerificationRequired);

            pendingUserAccountDAO.save(pendingUserAccount);
        }

        return pendingUserAccount;
    }

    /**
     * Associates an external account to a CAS user.
     */
    @Override
    public UserAccount associateAccountToUser(User user, AppType appType, String appName, String appUsername) throws AccountException {
        UserAccount account = findUserAccount(user, appName, appType);

        if (appType.isLinkageAllowed()) {
            ensureAccountIsNotLinkedToDifferentUser(appName, appType, appUsername, user);

            try {
                if (account == null) {
                    account = new UserAccount();
                    account.setUser(user);
                    account.setAppType(appType);
                    account.setAppName(appName);
                    account.setAppUsername(appUsername);
                } else {
                    account.setAppUsername(appUsername);
                    account.setDisabled(false);
                }

                userAccountDAO.save(account);
            } catch (Exception e) {
                throw new AccountException("Failed to associate user " + user + " to app account " + appUsername + " on " + appName + "/" + appType, e);
            }
        }

        return account;
    }

    /**
     * Tries to associate a user with a pending registration. If successful, this
     * will return the newly associated user account. If there's already a disabled user account matching the
     * registration (unlikely), re-enable it instead of creating a duplicate.
     */
    @Override
    public UserAccount associatePendingAccountToUser(User user, String registrationCode) throws AccountException {
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

                userDAO.save(user);
            }

            log.info("associated user " + user + " to " + account.getAppName() + "/" + account.getAppType());

            pendingUserAccountDAO.delete(pendingAccount);
        } catch (Exception e) {
            throw new AccountException("failed to associate user " + user + " to registration code " + registrationCode, e);
        }

        return account;
    }

    /**
     * Finds any linked user accounts to a given app and local username.
     * If a null or blank appUsername is passed, it will return all linked user accounts for that app name and type.
     */
    @Override
    public List<UserAccount> findEnabledUserAccounts(String appName, AppType appType, String appUsername) {
        if (StringUtils.isEmpty(appUsername)) {
            return userAccountDAO.findByAppNameAndAppTypeAndDisabled(appName, appType, false);
        } else {
            return userAccountDAO.findByAppNameAndAppTypeAndAppUsernameAndDisabled(appName, appType, appUsername, false);
        }
    }

    /**
     * Finds any linked user accounts to a given app and Global User ID.
     */
    @Override
    public List<UserAccount> findEnabledUserAccounts(String appName, AppType appType, long globalUserId) {
        return userAccountDAO.findByAppNameAndAppTypeAndUserIdAndDisabled(appName, appType, globalUserId, false);
    }

    /**
     * Finds any linked user accounts to a given app and local username.
     * If a null or blank appUsername is passed, it will return all linked user accounts for that app name and type.
     */
    @Override
    public List<UserAccount> findUserAccounts(String appName, AppType appType, String appUsername) {
        if (StringUtils.isEmpty(appUsername)) {
            return userAccountDAO.findByAppNameAndAppType(appName, appType);
        } else {
            return userAccountDAO.findByAppNameAndAppTypeAndAppUsername(appName, appType, appUsername);
        }
    }

    /**
     * Finds any linked user accounts to a given app and Global User ID.
     */
    @Override
    public List<UserAccount> findUserAccounts(String appName, AppType appType, long globalUserId) {
        return userAccountDAO.findByAppNameAndAppTypeAndUserId(appName, appType, globalUserId);
    }

    /**
     * Finds any linked user accounts to a given app and Global User ID.
     */
    @Override
    public UserAccount findUserAccountByInfusionsoftId(String appName, AppType appType, String infusionsoftId) {
        return userAccountDAO.findByAppNameAndAppTypeAndUser_Username(appName, appType, infusionsoftId);
    }

    /**
     * Finds any linked user accounts to a given app and local username that have been disabled.
     */
    @Override
    public List<UserAccount> findDisabledUserAccounts(String appName, AppType appType, String appUsername) {
        if (StringUtils.isEmpty(appUsername)) {
            return userAccountDAO.findByAppNameAndAppTypeAndDisabled(appName, appType, true);
        } else {
            return userAccountDAO.findByAppNameAndAppTypeAndAppUsernameAndDisabled(appName, appType, appUsername, true);
        }
    }

    /**
     * Finds any linked user accounts to a given app and Global User ID that have been disabled.
     */
    @Override
    public List<UserAccount> findDisabledUserAccounts(String appName, AppType appType, long globalUserId) {
        return userAccountDAO.findByAppNameAndAppTypeAndUserIdAndDisabled(appName, appType, globalUserId, true);
    }

    /**
     * Deletes a linked user account.
     */
    @Override
    public void deleteAccount(UserAccount account) {
        log.info("Deleting user account " + account.toString());

        User accountUser = account.getUser();
        if (!accountUser.getAccounts().remove(account)) {
            log.debug("Account not found on user to remove: " + account.toString());
        }
        userDAO.save(accountUser);
        userAccountDAO.delete(account);

        try {
            oauthService.revokeAccessTokensByUserAccount(account);
        } catch (OAuthException e) {
            log.error("Unable to revoke access tokens during account deletion -> " + account.toString());
        }
    }

    /**
     * Disables a linked user account.
     */
    @Override
    public void disableAccount(UserAccount account) {
        log.info("Disabling user account " + account.toString());

        account.setDisabled(true);
        userAccountDAO.save(account);
        userDAO.save(account.getUser());

        try {
            oauthService.revokeAccessTokensByUserAccount(account);
        } catch (OAuthException e) {
            log.error("Unable to revoke access tokens during account disabling -> " + account.toString());
        }
    }

    /**
     * Enables a linked user account that was previously disabled.
     */
    @Override
    public void enableUserAccount(UserAccount account) {
        log.info("Re-enabling user account " + account.toString());

        account.setDisabled(false);

        userAccountDAO.save(account);
        userDAO.save(account.getUser());
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
    public User findEnabledUser(String username) {
        return userDAO.findByUsernameAndEnabled(username, true);
    }

    @Override
    public void cleanupLoginAttempts() {
        log.info("cleaning up login attempts older than " + loginAttemptMaxAge + " ms");

        Date date = new Date(System.currentTimeMillis() - loginAttemptMaxAge);
        List<LoginAttempt> attempts = loginAttemptDAO.findByDateAttemptedLessThan(date);

        log.info("deleting " + attempts.size() + " login attempts that occurred before " + date);

        loginAttemptDAO.delete(attempts);
    }

    @Override
    public List<UserAccount> findByUserAndDisabled(User user, boolean disabled) {
        return userAccountDAO.findByUserAndDisabled(user, disabled);
    }

    @Override
    public boolean isDuplicateUsername(User user) {
        if (user == null) {
            return false;
        } else if (user.getId() == null) {
            return userDAO.findByUsername(user.getUsername()) != null;
        } else {
            return userDAO.findByUsernameAndIdNot(user.getUsername(), user.getId()) != null;
        }
    }

    /**
     * Changes the application username that is associated with a user
     */
    @Override
    public void changeAssociatedAppUsername(User user, String appName, AppType appType, String newAppUsername) throws AccountException {
        if (user != null) {
            UserAccount account = findUserAccount(user, appName, appType);
            if (account != null) {
                ensureAccountIsNotLinkedToDifferentUser(appName, appType, newAppUsername, user);
                String oldAppUsername = account.getAppUsername();
                account.setAppUsername(newAppUsername);
                userAccountDAO.save(account);
                log.info("Changed application username on " + appName + "/" + appType + " for CAS user " + user + " from " + oldAppUsername + " to " + newAppUsername);
            }
        }
    }

    private void ensureAccountIsNotLinkedToDifferentUser(String appName, AppType appType, String appUsername, User user) throws DuplicateAccountException {
        List<UserAccount> accounts = userAccountDAO.findByAppNameAndAppTypeAndAppUsernameAndUserNot(appName, appType, appUsername, user);

        if (accounts != null && !accounts.isEmpty()) {
            List<String> usernames = new ArrayList<String>();
            for (UserAccount account : accounts) {
                usernames.add(account.getUser().getUsername());
            }
            log.error("Account " + appUsername + " on " + appName + "/" + appType + " could not be linked to " + user + " since it is already linked to a different Infusionsoft ID (" + StringUtils.join(usernames, ", ") + ")");
            throw new DuplicateAccountException(accounts);
        }
    }
}

