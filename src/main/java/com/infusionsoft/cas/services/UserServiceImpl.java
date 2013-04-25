package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.*;
import com.infusionsoft.cas.domain.*;
import com.infusionsoft.cas.exceptions.AccountException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    AuthorityDAO authorityDAO;

    @Autowired
    LoginAttemptDAO loginAttemptDAO;

    @Autowired
    MailService mailService;

    @Autowired
    PasswordService passwordService;

    @Autowired
    PendingUserAccountDAO pendingUserAccountDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    UserAccountDAO userAccountDAO;

    @Autowired
    UserPasswordDAO userPasswordDAO;

    @Override
    public User addUser(User user) throws InfusionsoftValidationException {
        user.getAuthorities().add(authorityDAO.findByAuthority("ROLE_CAS_USER"));
        userDAO.save(user);

        passwordService.setPasswordForUser(user);

        return user;
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
    public void updateUser(User user) {
        //TODO: if this user is currently logged in then change the object in the security context
        userDAO.save(user);
    }

    @Override
    public void updateUserAccount(UserAccount userAccount) {
        userAccountDAO.save(userAccount);
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
        String recoveryCode = createPasswordRecoveryCode(user);

        log.info("password recovery code " + recoveryCode + " created for user " + user.getId());

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

        if(user != null && user.getPasswordRecoveryCodeCreatedTime().plusMinutes(30).isAfter(new DateTime())) {
            retVal = user;
        }

        return retVal;
    }

    /**
     * Creates a unique, random password recovery code for a user.
     */
    @Override
    public synchronized String createPasswordRecoveryCode(User user) {
        String recoveryCode = RandomStringUtils.randomAlphabetic(12).toUpperCase();

        while (findUserByRecoveryCode(recoveryCode) != null) {
            recoveryCode = RandomStringUtils.randomAlphabetic(12).toUpperCase();
        }

        user.setPasswordRecoveryCode(recoveryCode);
        user.setPasswordRecoveryCodeCreatedTime(new DateTime());

        userDAO.save(user);

        return user.getPasswordRecoveryCode();
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
    public UserAccount findUserAccount(User user, String appName, String appType) {
        return userAccountDAO.findByUserAndAppNameAndAppType(user, appName, appType);
    }

    /**
     * Returns a user's accounts, sorted by type and name for consistency.
     */
    @Override
    public List<UserAccount> findSortedUserAccounts(User user) {
        List<UserAccount> accounts = new ArrayList<UserAccount>();

        accounts.addAll(userAccountDAO.findByUserAndAppTypeAndDisabled(user, AppType.CRM, false));
        accounts.addAll(userAccountDAO.findByUserAndAppTypeAndDisabled(user, AppType.COMMUNITY, false));
        accounts.addAll(userAccountDAO.findByUserAndAppTypeAndDisabled(user, AppType.CUSTOMERHUB, false));

        return accounts;
    }

    /**
     * Creates a pending user account for someone, so they can come back and complete registration later.
     * If one is already pending, regurgitate the same code.
     */
    @Override
    public PendingUserAccount createPendingUserAccount(String appType, String appName, String appUsername, String
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
    public UserAccount associateAccountToUser(User user, String appType, String appName, String appUsername) throws AccountException {
        ensureAccountIsNotLinkedToDifferentUser(appName, appType, appUsername, user);
        UserAccount account = findUserAccount(user, appName, appType);

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
            throw new AccountException("failed to associate user to app account", e);
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

            log.info("associated user " + user.getId() + " to " + account.getAppName() + "/" + account.getAppType());

            pendingUserAccountDAO.delete(pendingAccount);
        } catch (Exception e) {
            throw new AccountException("failed to associate user " + user.getId() + " to registration code " + registrationCode, e);
        }

        return account;
    }

    /**
     * Finds any linked user accounts to a given app and local username.
     * If a null or blank appUsername is passed, it will return all linked user accounts for that app name and type.
     */
    @Override
    public List<UserAccount> findEnabledUserAccounts(String appName, String appType, String appUsername) {
        if (StringUtils.isEmpty(appUsername)) {
            return userAccountDAO.findByAppNameAndAppTypeAndDisabled(appName, appType, false);
        } else {
            return userAccountDAO.findByAppNameAndAppTypeAndAppUsernameAndDisabled(appName, appType, appUsername, false);
        }
    }

    /**
     * Finds any linked user accounts to a given app and CAS global ID.
     */
    @Override
    public List<UserAccount> findEnabledUserAccounts(String appName, String appType, long casGlobalId) {
        return userAccountDAO.findByAppNameAndAppTypeAndUserIdAndDisabled(appName, appType, casGlobalId, false);
    }

    /**
     * Finds any linked user accounts to a given app and local username.
     * If a null or blank appUsername is passed, it will return all linked user accounts for that app name and type.
     */
    @Override
    public List<UserAccount> findUserAccounts(String appName, String appType, String appUsername) {
        if (StringUtils.isEmpty(appUsername)) {
            return userAccountDAO.findByAppNameAndAppType(appName, appType);
        } else {
            return userAccountDAO.findByAppNameAndAppTypeAndAppUsername(appName, appType, appUsername);
        }
    }

    /**
     * Finds any linked user accounts to a given app and CAS global ID.
     */
    @Override
    public List<UserAccount> findUserAccounts(String appName, String appType, long casGlobalId) {
        return userAccountDAO.findByAppNameAndAppTypeAndUserId(appName, appType, casGlobalId);
    }

    /**
     * Finds any linked user accounts to a given app and local username that have been disabled.
     */
    @Override
    public List<UserAccount> findDisabledUserAccounts(String appName, String appType, String appUsername) {
        if (StringUtils.isEmpty(appUsername)) {
            return userAccountDAO.findByAppNameAndAppTypeAndDisabled(appName, appType, true);
        } else {
            return userAccountDAO.findByAppNameAndAppTypeAndAppUsernameAndDisabled(appName, appType, appUsername, true);
        }
    }

    /**
     * Finds any linked user accounts to a given app and CAS global ID that have been disabled.
     */
    @Override
    public List<UserAccount> findDisabledUserAccounts(String appName, String appType, long casGlobalId) {
        return userAccountDAO.findByAppNameAndAppTypeAndUserIdAndDisabled(appName, appType, casGlobalId, true);
    }

    /**
     * Deletes a linked user account.
     */
    @Override
    public void deleteAccount(UserAccount account) {
        log.info("Deleting user account " + account.getId());

        User accountUser = account.getUser();
        if (!accountUser.getAccounts().remove(account))
            log.debug("Account not found on user to remove: " + account.getId());
        userDAO.save(accountUser);
        userAccountDAO.delete(account);
    }

    /**
     * Disables a linked user account.
     */
    @Override
    public void disableAccount(UserAccount account) {
        log.info("disabling user account " + account.getId());

        account.setDisabled(true);

        userAccountDAO.save(account);
        userDAO.save(account.getUser());
    }

    /**
     * Enables a linked user account that was previously disabled.
     */
    @Override
    public void enableUserAccount(UserAccount account) {
        log.info("re-enabling user account " + account.getId());

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
        long loginAttemptMaxAge = 86400000;
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
    public boolean isDuplicateUsername(String username, Long id) {
        return userDAO.findByUsernameAndIdNot(username, id) != null;
    }

    /**
     * Changes the application username that is associated with a user
     */
    @Override
    public void changeAssociatedAppUsername(User user, String appName, String appType, String newAppUsername) throws AccountException {
        if (user != null) {
            UserAccount account = findUserAccount(user, appName, appType);
            if (account != null) {
                ensureAccountIsNotLinkedToDifferentUser(appName, appType, newAppUsername, user);
                String oldAppUsername = account.getAppUsername();
                account.setAppUsername(newAppUsername);
                userAccountDAO.save(account);
                log.info("Changed application username on " + appName + "/" + appType + " for CAS user " + user.getUsername() + " from " + oldAppUsername + " to " + newAppUsername);
            }
        }
    }

    private void ensureAccountIsNotLinkedToDifferentUser(String appName, String appType, String appUsername, User user) throws AccountException {
        List<UserAccount> accounts = userAccountDAO.findByAppNameAndAppTypeAndAppUsernameAndUserNot(appName, appType, appUsername, user);
        if (accounts != null && !accounts.isEmpty())
            throw new AccountException("Account " + appUsername + " on " + appName + "/" + appType + " is already linked to a different Infusionsoft ID", null);
    }
}

