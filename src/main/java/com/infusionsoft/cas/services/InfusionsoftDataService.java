package com.infusionsoft.cas.services;

import com.infusionsoft.cas.exceptions.CASMappingException;
import com.infusionsoft.cas.types.*;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for simple lower-level data access stuff.
 */
public class InfusionsoftDataService {
    private static final Logger log = Logger.getLogger(InfusionsoftAuthenticationService.class);

    private HibernateTemplate hibernateTemplate;

    /**
     * Creates a unique, random password recovery code for a user.
     */
    public synchronized String createPasswordRecoveryCode(User user) {
        String recoveryCode = RandomStringUtils.randomAlphabetic(12).toUpperCase();

        while (findUserByRecoveryCode(recoveryCode) != null) {
            recoveryCode = RandomStringUtils.randomAlphabetic(12).toUpperCase();
        }

        user.setPasswordRecoveryCode(recoveryCode);

        hibernateTemplate.update(user);

        return user.getPasswordRecoveryCode();
    }

    /**
     * Attempts to find a user by their recovery code.
     */
    public User findUserByRecoveryCode(String recoveryCode) {
        List<User> users = (List<User>) hibernateTemplate.find("from User where passwordRecoveryCode = ?", recoveryCode);

        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }

    /**
     * Finds a user account by id, but only if it belongs to a given user.
     */
    public UserAccount findUserAccount(User user, Long accountId) {
        List<UserAccount> accounts = (List<UserAccount>) hibernateTemplate.find("from UserAccount where user = ? and id = ?", user, accountId);

        if (accounts.size() > 0) {
            return accounts.get(0);
        } else {
            return null;
        }
    }

    /**
     * Finds a user account by for a user.
     */
    public UserAccount findUserAccount(User user, String appName, String appType, String appUsername) {
        List<UserAccount> accounts = (List<UserAccount>) hibernateTemplate.find("from UserAccount ua where ua.user = ? and ua.appName = ? and ua.appType = ? and ua.appUsername = ?", user, appName, appType, appUsername);

        if (accounts.size() > 0) {
            return accounts.get(0);
        } else {
            return null;
        }
    }

    /**
     * Returns a user's accounts, sorted by type and name for consistency.
     */
    public List<UserAccount> findSortedUserAccounts(User user) {
        List<UserAccount> accounts = new ArrayList<UserAccount>();

        accounts.addAll(hibernateTemplate.find("from UserAccount where user = ? and appType = ? and disabled = ? order by appName", user, AppType.CRM, false));
        accounts.addAll(hibernateTemplate.find("from UserAccount where user = ? and appType = ? and disabled = ? order by appName", user, AppType.COMMUNITY, false));
        accounts.addAll(hibernateTemplate.find("from UserAccount where user = ? and appType = ? and disabled = ? order by appName", user, AppType.CUSTOMERHUB, false));

        return accounts;
    }

    /**
     * Creates a pending user account for someone, so they can come back and complete registration later.
     * If one is already pending, regurgitate the same code.
     */
    public PendingUserAccount createPendingUserAccount(String appType, String appName, String appUsername, String firstName, String lastName, String email, boolean passwordVerificationRequired) {
        List<PendingUserAccount> matches = hibernateTemplate.find("from PendingUserAccount where appType = ? and appName = ? and appUsername = ?", appType, appName, appUsername);
        PendingUserAccount account = null;

        if (matches.size() > 0) {
            account = matches.get(0);
            account.setFirstName(firstName);
            account.setLastName(lastName);
            account.setEmail(email);
            account.setPasswordVerificationRequired(passwordVerificationRequired);

            hibernateTemplate.update(account);
        } else {
            account = new PendingUserAccount();

            account.setAppName(appName);
            account.setAppType(appType);
            account.setAppUsername(appUsername);
            account.setRegistrationCode(appName + "-" + RandomStringUtils.random(16, true, true));
            account.setFirstName(firstName);
            account.setLastName(lastName);
            account.setEmail(email);
            account.setPasswordVerificationRequired(passwordVerificationRequired);

            hibernateTemplate.save(account);
        }

        return account;
    }

    /**
     * Associates an external account to a CAS user.
     */
    public UserAccount associateAccountToUser(User user, String appType, String appName, String appUsername) throws CASMappingException {
        UserAccount account = findUserAccount(user, appName, appType, appUsername);

        try {
            if (account == null) {
                account = new UserAccount();
                account.setUser(user);
                account.setAppType(appType);
                account.setAppName(appName);
                account.setAppUsername(appUsername);

                user.getAccounts().add(account);

                hibernateTemplate.save(account);
                hibernateTemplate.update(user);
            } else {
                account.setDisabled(false);

                hibernateTemplate.update(account);
            }
        } catch (Exception e) {
            throw new CASMappingException("failed to associate user to app account", e);
        }

        return account;
    }

    /**
     * Tries to associate a user with a pending registration. If successful, this
     * will return the newly associated user account. If there's already a disabled user account matching the
     * registration (unlikely), re-enable it instead of creating a duplicate.
     */
    public UserAccount associatePendingAccountToUser(User user, String registrationCode) throws CASMappingException {
        PendingUserAccount pendingAccount = findPendingUserAccount(registrationCode);
        UserAccount account = findUserAccount(user, pendingAccount.getAppName(), pendingAccount.getAppType(), pendingAccount.getAppUsername());

        try {
            if (account == null) {
                account = new UserAccount();

                account.setUser(user);
                account.setAppName(pendingAccount.getAppName());
                account.setAppType(pendingAccount.getAppType());
                account.setAppUsername(pendingAccount.getAppUsername());

                user.getAccounts().add(account);

                hibernateTemplate.save(account);
                hibernateTemplate.update(user);
            } else {
                account.setDisabled(false);

                hibernateTemplate.update(user);
            }

            log.info("associated user " + user.getId() + " to " + account.getAppName() + "/" + account.getAppType());

            hibernateTemplate.delete(pendingAccount);
        } catch (Exception e) {
            throw new CASMappingException("failed to associate user " + user.getId() + " to registration code " + registrationCode, e);
        }

        return account;
    }

    /**
     * Finds any linked user accounts to a given app and local username. It's possible multiple Infusionsoft IDs
     * may be linked to the same local username on the same app. If a null or blank appUsername is passed, it will
     * return all linked user accounts for that app name and type.
     */
    public List<UserAccount> findEnabledUserAccounts(String appName, String appType, String appUsername) {
        if (StringUtils.isEmpty(appUsername)) {
            return hibernateTemplate.find("from UserAccount ua where ua.appName = ? and ua.appType = ? and ua.disabled = ?", appName, appType, false);
        } else {
            return hibernateTemplate.find("from UserAccount ua where ua.appName = ? and ua.appType = ? and ua.appUsername = ? and ua.disabled = ?", appName, appType, appUsername, false);
        }
    }

    /**
     * Finds any linked user accounts to a given app and local username that have been disabled.
     */
    public List<UserAccount> findDisabledUserAccounts(String appName, String appType, String appUsername) {
        if (StringUtils.isEmpty(appUsername)) {
            return hibernateTemplate.find("from UserAccount ua where ua.appName = ? and ua.appType = ? and ua.disabled = ?", appName, appType, true);
        } else {
            return hibernateTemplate.find("from UserAccount ua where ua.appName = ? and ua.appType = ? and ua.appUsername = ? and ua.disabled = ?", appName, appType, appUsername, true);
        }
    }

    /**
     * Disables a linked user account.
     */
    public void disableAccount(UserAccount account) {
        log.info("disabling user account " + account.getId());

        account.setDisabled(true);

        hibernateTemplate.update(account);
        hibernateTemplate.update(account.getUser());
    }

    /**
     * Enables a linked user account that was previously disabled.
     */
    public void enableUserAccount(UserAccount account) {
        log.info("re-enabling user account " + account.getId());

        account.setDisabled(false);

        hibernateTemplate.update(account);
        hibernateTemplate.update(account.getUser());
    }

    /**
     * Finds a pending user account by its unique registration code.
     */
    public PendingUserAccount findPendingUserAccount(String registrationCode) {
        List<PendingUserAccount> accounts = hibernateTemplate.find("from PendingUserAccount where registrationCode = ?", registrationCode);

        if (accounts.size() > 0) {
            return accounts.get(0);
        } else {
            return null;
        }
    }

    /**
     * Finds a user by username.
     */
    public User findUser(String username) {
        List<User> users = hibernateTemplate.find("from User u where lower(u.username) = ? and u.enabled = true", username.toLowerCase());

        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }

    /**
     * Finds a user by username and (already MD5-encrypted) password.
     */
    public User findUser(String username, String md5password) {
        List<UserPassword> passwords = hibernateTemplate.find("from UserPassword p where lower(p.user.username) = ? and p.passwordEncodedMD5 = ? and p.active = true and p.user.enabled = true", username.toLowerCase(), md5password);

        if (passwords.size() > 0) {
            return passwords.get(0).getUser();
        } else {
            return null;
        }
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
