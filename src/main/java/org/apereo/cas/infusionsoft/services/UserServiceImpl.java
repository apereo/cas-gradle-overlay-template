package org.apereo.cas.infusionsoft.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apereo.cas.infusionsoft.dao.AuthorityDAO;
import org.apereo.cas.infusionsoft.dao.LoginAttemptDAO;
import org.apereo.cas.infusionsoft.dao.UserAccountDAO;
import org.apereo.cas.infusionsoft.dao.UserDAO;
import org.apereo.cas.infusionsoft.domain.*;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.apereo.cas.infusionsoft.web.ValidationUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
    private UserDAO userDAO;

    @Autowired
    private UserAccountDAO userAccountDAO;

    @Value("${infusionsoft.cas.garbageman.loginattemptmaxage}")
    private long loginAttemptMaxAge = 86400000; // default to 1 day

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
     * Finds an enabled user by username.
     */
    @Override
    @Deprecated
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

}

