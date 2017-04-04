package org.apereo.cas.infusionsoft.services;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.infusionsoft.dao.AuthorityDAO;
import org.apereo.cas.infusionsoft.dao.LoginAttemptDAO;
import org.apereo.cas.infusionsoft.dao.UserAccountDAO;
import org.apereo.cas.infusionsoft.dao.UserDAO;
import org.apereo.cas.infusionsoft.domain.*;
import org.apereo.cas.infusionsoft.events.UserAccountRemovedEvent;
import org.apereo.cas.infusionsoft.exceptions.DuplicateAccountException;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftAccountException;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.apereo.cas.infusionsoft.web.ValidationUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(transactionManager = "transactionManager")
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private AuthorityDAO authorityDAO;

    @Autowired
    private CrmService crmService;

    @Autowired
    private LoginAttemptDAO loginAttemptDAO;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserAccountDAO userAccountDAO;

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
    public Page<UserAccount> findUserAccountsByUsernameLikeOrAppNameLikeAndAppType(String username, String appName, AppType appType, Pageable pageable) {
        if (StringUtils.isEmpty(username)) {
            username = "%";
        } else if (StringUtils.contains(username, "*") || StringUtils.contains(username, "%")) {
            username = username.replace("*", "%");
        } else {
            username = "%" + username + "%";
        }

        if (StringUtils.isEmpty(appName)) {
            appName = "%";
        } else if (StringUtils.contains(appName, "*") || StringUtils.contains(appName, "%")) {
            appName = appName.replace("*", "%");
        } else {
            appName = "%" + appName + "%";
        }

        return userAccountDAO.findByUser_UsernameLikeOrAppNameLikeAndAppType(username, appName, appType, pageable);
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
    public Map<AppType, List<UserAccount>> findSortedUserAccounts(User user) {
        Map<AppType, List<UserAccount>> retVal = new LinkedHashMap<AppType, List<UserAccount>>();

        UserAccount marketplaceUserAccount = new UserAccount();
        marketplaceUserAccount.setAppType(AppType.MARKETPLACE);

        List<UserAccount> marketplaceList = new ArrayList<UserAccount>();
        marketplaceList.add(marketplaceUserAccount);

        retVal.put(AppType.CRM, userAccountDAO.findByUserAndAppTypeAndDisabledOrderByAppNameAsc(user, AppType.CRM, false));
        retVal.put(AppType.CUSTOMERHUB, userAccountDAO.findByUserAndAppTypeAndDisabledOrderByAppNameAsc(user, AppType.CUSTOMERHUB, false));
        retVal.put(AppType.COMMUNITY, userAccountDAO.findByUserAndAppTypeAndDisabledOrderByAppNameAsc(user, AppType.COMMUNITY, false));
        retVal.put(AppType.MARKETPLACE, marketplaceList);

        return retVal;
    }

    /**
     * Returns a user's accounts, sorted by type and name for consistency when displaying lists of connected accounts.
     */
    @Override
    public List<UserAccount> findSortedUserAccountsByAppType(User user, AppType appType) {
        return userAccountDAO.findByUserAndAppTypeAndDisabledOrderByAppNameAsc(user, appType, false);
    }

    /**
     * Associates an external account to a CAS user.
     */
    @Override
    public UserAccount associateAccountToUser(User user, AppType appType, String appName, String appUsername) throws InfusionsoftAccountException {
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
                throw new InfusionsoftAccountException("Failed to associate user " + user + " to app account " + appUsername + " on " + appName + "/" + appType, e);
            }
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

        applicationEventPublisher.publishEvent(new UserAccountRemovedEvent(account));
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

        applicationEventPublisher.publishEvent(new UserAccountRemovedEvent(account));
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
    public List<UserAccount> findActiveUserAccounts(User user) {
        return userAccountDAO.findByUserAndDisabled(user, false);
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
    public void changeAssociatedAppUsername(User user, String appName, AppType appType, String newAppUsername) throws InfusionsoftAccountException {
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

    @Override
    public boolean validateUserApplication(String application) {
        boolean retVal = true;

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserAccount> accounts = findSortedUserAccountsByAppType(user, AppType.CRM);
        List<String> crmAccounts = crmService.extractAppNames(accounts);

        if (!crmAccounts.contains(application)) {
            log.error("User " + SecurityContextHolder.getContext().getAuthentication().getName() + " tried to gain access to the application " + application);
            retVal = false;
        }

        return retVal;
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

