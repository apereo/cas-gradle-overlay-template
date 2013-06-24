package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.UserPasswordDAO;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserPassword;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    private static final String PASSWORD_TOOSHORT_CODE = "password.tooshort";
    private static final String PASSWORD_WEIRDCHARS_CODE = "password.weirdcharacters";
    private static final String PASSWORD_NUMBERANDLETTER_CODE = "password.numberandletter";
    private static final String PASSWORD_UPPERCASE_CODE = "password.uppercase";
    private static final String PASSWORD_CONTAINUSERNAME_CODE = "password.nousername";
    private static final String PASSWORD_CANTMATCH_CODE = "password.cantmatch";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserPasswordDAO userPasswordDAO;

    /**
     * Checks if a user's existing password is valid. We need this for when an already logged in user wants
     * to update his user profile.
     */
    @Override
    public boolean isPasswordValid(String username, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        UserPassword userPassword = userPasswordDAO.findByUser_UsernameAndPasswordEncodedAndActiveTrue(username, encodedPassword);

        log.debug("checking if encoded password " + encodedPassword + " is valid for user " + username + ": " + (userPassword != null));

        return userPassword != null;
    }

    /**
     * Checks if the passwords match
     */
    @Override
    public boolean passwordsMatch(UserPassword userPassword, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return StringUtils.equals(userPassword.getPasswordEncoded(), encodedPassword);
    }

    /**
     * Checks if the MD5 encoded passwords match
     */
    @Override
    public boolean md5PasswordsMatch(UserPassword userPassword, String passwordEncodedMD5) {
        return StringUtils.equals(userPassword.getPasswordEncodedMD5(), passwordEncodedMD5);
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
     * Determines how many days a password will expire.
     */
    @Override
    public int getNumberOfDaysToPasswordExpirationDate(String userId) {
        UserPassword userPassword = userPasswordDAO.findByUser_UsernameAndActiveTrue(userId);

        DateMidnight createdDateTime = new DateTime(userPassword.getDateCreated().getTime()).toDateMidnight();
        DateMidnight expirationDateTime = createdDateTime.plusDays(DEFAULT_PASSWORD_EXPIRE_DAYS);
        DateMidnight today = new DateMidnight();
        Days expiresInDays = Days.daysBetween(today, expirationDateTime);

        return expiresInDays.getDays();
    }

    /**
     * Fetches the current encoded password for this user, or null if none is
     * valid.
     */
    @Override
    public UserPassword getPasswordForUser(User user) {
        return userPasswordDAO.findByUser_UsernameAndActiveTrue(user.getUsername());
    }

    /**
     * Sets a new password and invalidates the previous ones.
     */
    @Override
    public void setPasswordForUser(User user) {
        if (validatePassword(user) == null) {
            UserPassword userPassword = new UserPassword();

            userPassword.setUser(user);
            userPassword.setPasswordEncoded(passwordEncoder.encode(user.getPassword()));
            userPassword.setPasswordEncodedMD5(DigestUtils.md5Hex(user.getPassword()));
            // TODO: use UTC date here
            userPassword.setDateCreated(new Date());
            userPassword.setActive(true);

            for (UserPassword p : user.getPasswords()) {
                p.setActive(false);
                userPasswordDAO.save(p);
            }

            userPasswordDAO.save(userPassword);

            log.debug("Set password for user " + user.getId());
        }
    }

    /**
     * Validates a password and returns a validation error, if any.
     */
    @Override
    public String validatePassword(User user) {
        String password = user.getPassword();
        String username = user.getUsername();

        if (StringUtils.isEmpty(password) || password.length() < 7) {
            return PASSWORD_TOOSHORT_CODE;
        }

        if (containsNonAsciiChars(password)) {
            return PASSWORD_WEIRDCHARS_CODE;
        }

        if (!StringUtils.containsAny(password, LETTERS) || !StringUtils.containsAny(password, DIGITS)) {
            return PASSWORD_NUMBERANDLETTER_CODE;
        }

        if (!StringUtils.containsAny(password, "ABCDEFGHIJKLMNOPQRSTUVWXYZ")) {
            return PASSWORD_UPPERCASE_CODE;
        }

        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            String usernameLower = username.toLowerCase();
            String passwordLower = password.toLowerCase();

            if (passwordLower.equals(usernameLower) || passwordLower.contains(usernameLower)) {
                return PASSWORD_CONTAINUSERNAME_CODE;
            }
        }

        if (user.getId() != null && lastFourPasswordsContains(user, password)) {
            return PASSWORD_CANTMATCH_CODE;
        }

        return null;
    }

    /**
     * Checks if a password matches any of the last four passwords.
     */
    private boolean lastFourPasswordsContains(User user, String password) {
        String passwordEncoded = passwordEncoder.encode(password);
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
