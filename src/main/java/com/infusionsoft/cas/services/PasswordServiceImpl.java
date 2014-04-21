package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.UserPasswordDAO;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserPassword;
import com.infusionsoft.cas.exceptions.InfusionsoftValidationException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Service that validates all our funky password rules.
 */
@Service
@Transactional
public class PasswordServiceImpl implements PasswordService {
    private static final Logger log = Logger.getLogger(PasswordServiceImpl.class);

    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String LETTERS = UPPERCASE_LETTERS + LOWERCASE_LETTERS;
    private static final String DIGITS = "0123456789";

    private static final int DEFAULT_PASSWORD_EXPIRE_DAYS = 90;
    private static final long DEFAULT_PASSWORD_EXPIRE_MS = DEFAULT_PASSWORD_EXPIRE_DAYS * 86400000L;

    private static final String PASSWORD_TOOSHORT_CODE = "password.error.tooshort";
    private static final String PASSWORD_WEIRDCHARS_CODE = "password.error.weirdcharacters";
    private static final String PASSWORD_NUMBERANDLETTER_CODE = "password.error.numberandletter";
    private static final String PASSWORD_UPPERCASE_CODE = "password.error.uppercase";
    private static final String PASSWORD_CONTAINUSERNAME_CODE = "password.error.nousername";
    private static final String PASSWORD_CANTMATCH_CODE = "password.error.cantmatch";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserPasswordDAO userPasswordDAO;

    /**
     * Checks if a user's existing password is correct. We need this for when an already logged in user wants
     * to update his user profile.
     */
    @Override
    public boolean isPasswordCorrect(User user, String password) {
        UserPassword userPassword = getActivePasswordForUser(user);
        boolean isPasswordCorrect = userPassword != null && StringUtils.equals(userPassword.getPasswordEncoded(), encodePassword(password));
        if (isPasswordCorrect) {
            log.debug("Password is valid for user " + user);
        } else {
            log.debug("Password is not valid for user " + user);
        }
        return isPasswordCorrect;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Returns a UserPassword where the user and passwords match. The password might be active or inactive.
     * If there is more than one password that matches, the first one that matches will be returned, ordered by
     * whether the password is active (active first, then inactive) and date created (most recently created first).
     */
    @Override
    public UserPassword getMatchingPasswordForUser(User user, String password) {
        List<UserPassword> userPasswords = userPasswordDAO.findByUserAndPasswordEncodedOrderByActiveDescDateCreatedDesc(user, encodePassword(password));
        UserPassword userPassword = getFirstPassword(userPasswords);
        if (userPassword == null) {
            log.debug("No matching password found for user " + user);
        } else if (userPassword.isActive()) {
            log.debug("Active matching password found for user " + user);
        } else {
            log.debug("Inactive matching password found for user " + user);
        }
        return userPassword;
    }

    /**
     * Returns a UserPassword where the user and MD5 encoded passwords match. The password might be active or inactive.
     * If there is more than one password that matches, the first one that matches will be returned, ordered by
     * whether the password is active (active first, then inactive) and date created (most recently created first).
     */
    @Override
    public UserPassword getMatchingMD5PasswordForUser(User user, String passwordEncodedMD5) {
        List<UserPassword> userPasswords = userPasswordDAO.findByUserAndPasswordEncodedMD5OrderByActiveDescDateCreatedDesc(user, passwordEncodedMD5);
        UserPassword userPassword = getFirstPassword(userPasswords);
        if (userPassword == null) {
            log.debug("No matching MD5 password found for user " + user);
        } else if (userPassword.isActive()) {
            log.debug("Active matching MD5 password found for user " + user);
        } else {
            log.debug("Inactive matching MD5 password found for user " + user);
        }
        return userPassword;
    }

    private UserPassword getFirstPassword(List<UserPassword> userPasswords) {
        if (userPasswords.isEmpty()) {
            return null;
        } else {
            return userPasswords.iterator().next();
        }
    }

    /**
     * Checks whether a password is expired.
     */
    @Override
    public boolean isPasswordExpired(UserPassword password) {
        long expiration = password.getDateCreated().getTime() + DEFAULT_PASSWORD_EXPIRE_MS;

        return expiration < System.currentTimeMillis();
    }

    /**
     * Fetches the current encoded password for this user, or null if none is
     * active.
     */
    @Override
    public UserPassword getActivePasswordForUser(User user) {
        return userPasswordDAO.findByUserAndActiveTrue(user);
    }

    /**
     * Sets a new password and invalidates the previous ones. Also clears out any recovery codes that might have been set.
     */
    @Override
    public void setPasswordForUser(User user, String plainTextPassword) throws InfusionsoftValidationException {
        String passwordError = validatePassword(user, plainTextPassword);
        if (passwordError == null) {
            UserPassword userPassword = new UserPassword();

            userPassword.setUser(user);
            userPassword.setPasswordEncoded(encodePassword(plainTextPassword));
            userPassword.setPasswordEncodedMD5(DigestUtils.md5Hex(plainTextPassword));
            // TODO: use UTC date here
            userPassword.setDateCreated(new Date());
            userPassword.setActive(true);

            for (UserPassword p : user.getPasswords()) {
                p.setActive(false);
                userPasswordDAO.save(p);
            }

            userPasswordDAO.save(userPassword);

            log.debug("Set password for user " + user.getId());
        } else {
            throw new InfusionsoftValidationException(passwordError);
        }
    }

    /**
     * Validates a password and returns a validation error, if any.
     */
    @Override
    public String validatePassword(User user, String plainTextPassword) {
        String username = user.getUsername();

        if (StringUtils.isEmpty(plainTextPassword) || plainTextPassword.length() < 7) {
            return PASSWORD_TOOSHORT_CODE;
        }

        if (containsNonAsciiChars(plainTextPassword)) {
            return PASSWORD_WEIRDCHARS_CODE;
        }

        if (!StringUtils.containsAny(plainTextPassword, LETTERS) || !StringUtils.containsAny(plainTextPassword, DIGITS)) {
            return PASSWORD_NUMBERANDLETTER_CODE;
        }

        if (!StringUtils.containsAny(plainTextPassword, UPPERCASE_LETTERS)) {
            return PASSWORD_UPPERCASE_CODE;
        }

        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(plainTextPassword)) {
            String usernameLower = username.toLowerCase();
            String passwordLower = plainTextPassword.toLowerCase();

            if (passwordLower.equals(usernameLower) || passwordLower.contains(usernameLower)) {
                return PASSWORD_CONTAINUSERNAME_CODE;
            }
        }

        if (user.getId() != null && lastFourPasswordsContains(user, plainTextPassword)) {
            return PASSWORD_CANTMATCH_CODE;
        }

        return null;
    }

    /**
     * Checks if a password matches any of the last four passwords.
     */
    public boolean lastFourPasswordsContains(User user, String password) {
        String passwordEncoded = encodePassword(password);
        Page<UserPassword> passwords = userPasswordDAO.findByUser(user, new PageRequest(0, 4, Sort.Direction.DESC, "DateCreated"));

        for (int i = 0; i < passwords.getNumberOfElements(); i++) {
            if (passwords.getContent().get(i).getPasswordEncoded().equals(passwordEncoded)) {
                return true;
            }
        }

        return false;
    }

    private boolean containsNonAsciiChars(String input) {
        boolean rtn = false;

        if (input != null) {
            for (int i = 0; i < input.length() && !rtn; i++) {
                if (input.charAt(i) > 255) {
                    rtn = true;
                }
            }
        }

        return rtn;
    }
}
