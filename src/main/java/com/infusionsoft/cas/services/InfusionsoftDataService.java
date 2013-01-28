package com.infusionsoft.cas.services;

import com.infusionsoft.cas.exceptions.CASMappingException;
import com.infusionsoft.cas.types.*;
import org.apache.commons.lang.RandomStringUtils;
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
     * Fetches details for a community account, if available.
     */
    public CommunityAccountDetails findCommunityAccountDetails(UserAccount account) {
        List<CommunityAccountDetails> details = (List<CommunityAccountDetails>) hibernateTemplate.find("from CommunityAccountDetails where userAccount = ?", account);

        if (details.size() > 0) {
            return details.get(0);
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
     * Returns a user's accounts, sorted by type and name for consistency.
     */
    public List<UserAccount> findSortedUserAccounts(User user) {
        List<UserAccount> accounts = new ArrayList<UserAccount>();

        accounts.addAll(hibernateTemplate.find("from UserAccount where user = ? and appType = ? order by appName", user, AppType.CRM));
        accounts.addAll(hibernateTemplate.find("from UserAccount where user = ? and appType = ? order by appName", user, AppType.COMMUNITY));
        accounts.addAll(hibernateTemplate.find("from UserAccount where user = ? and appType = ? order by appName", user, AppType.CUSTOMERHUB));

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
        UserAccount account = new UserAccount();

        account.setUser(user);
        account.setAppType(appType);
        account.setAppName(appName);
        account.setAppUsername(appUsername);

        user.getAccounts().add(account);

        try {
            hibernateTemplate.save(account);
            hibernateTemplate.update(user);
        } catch (Exception e) {
            throw new CASMappingException("failed to associate user to app account", e);
        }

        return account;
    }

    /**
     * Tries to associate a user with a pending registration. If successful, this
     * will return the newly associated user account.
     */
    public UserAccount associatePendingAccountToUser(User user, String registrationCode) throws CASMappingException {
        PendingUserAccount pendingAccount = findPendingUserAccount(registrationCode);
        UserAccount account = new UserAccount();

        account.setUser(user);
        account.setAppName(pendingAccount.getAppName());
        account.setAppType(pendingAccount.getAppType());
        account.setAppUsername(pendingAccount.getAppUsername());

        user.getAccounts().add(account);

        try {
            hibernateTemplate.save(account);
            hibernateTemplate.update(user);
            hibernateTemplate.delete(pendingAccount);

            log.info("associated new user to " + account.getAppName() + "/" + account.getAppType());
        } catch (Exception e) {
            throw new CASMappingException("failed to associate new user to registration code " + registrationCode, e);
        }

        return account;
    }

    /**
     * Finds any linked user accounts to a given app and local username. It's possible multiple Infusionsoft IDs
     * may be linked to the same local username on the same app.
     */
    public List<UserAccount> findLinkedUserAccounts(String appName, String appType, String appUsername) {
        return hibernateTemplate.find("from UserAccount ua where ua.appName = ? and ua.appType = ? and ua.appUsername = ?", appName, appType, appUsername);
    }

    /**
     * Disassociates an account (deletes it).
     */
    public void disassociateAccount(UserAccount account) {
        log.info("disassociating user account " + account.getId());
        CommunityAccountDetails details = findCommunityAccountDetails(account);

        if (details != null) {
            hibernateTemplate.delete(details);
        }

        account.getUser().getAccounts().remove(account);

        hibernateTemplate.update(account.getUser());
        hibernateTemplate.delete(account);
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
     * Finds a user by username and (already MD5-encrypted) password.
     */
    public User findUser(String username, String md5password) {
        List<UserPassword> passwords = hibernateTemplate.find("from UserPassword p where p.user.username = ? and p.passwordEncodedMD5 = ? and p.active = true", username, md5password);

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
