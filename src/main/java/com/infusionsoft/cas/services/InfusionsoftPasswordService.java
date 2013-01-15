package com.infusionsoft.cas.services;

import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserPassword;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.Date;
import java.util.List;

public class InfusionsoftPasswordService {
    private static final Logger log = Logger.getLogger(InfusionsoftPasswordService.class);

    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String LETTERS = UPPERCASE_LETTERS + LOWERCASE_LETTERS;
    private static final String DIGITS = "0123456789";

    private static final long DEFAULT_PASSWORD_EXPIRE_DAYS = 90;
    private static final long DEFAULT_PASSWORD_EXPIRE_MS = DEFAULT_PASSWORD_EXPIRE_DAYS * 86400000L;

    private static final String PASSWORD_TOOSHORT_CODE = "password.tooshort";
    private static final String PASSWORD_WEIRDCHARS_CODE = "password.weirdcharacters";
    private static final String PASSWORD_NUMBERANDLETTER_CODE = "password.numberandletter";
    private static final String PASSWORD_UPPERCASE_CODE = "password.uppercase";
    private static final String PASSWORD_CONTAINUSERNAME_CODE = "password.nousername";
    private static final String PASSWORD_CANTMATCH_CODE = "password.cantmatch";

    private HibernateTemplate hibernateTemplate;
    private PasswordEncoder passwordEncoder;

    /**
     * Checks if a user's existing password is valid. We need this for when an already logged in user wants
     * to update his user profile.
     */
    public boolean isPasswordValid(User user, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        List<User> users = hibernateTemplate.find("from UserPassword p where p.user = ? and p.passwordEncoded = ? and p.active = true", user, encodedPassword);

        return users.size() > 0;
    }

    /**
     * Tells if a user's password is expired.
     */
    public boolean isPasswordExpired(User user) {
        return isPasswordExpired(getPasswordForUser(user));
    }

    /**
     * Checks whether a password is expired.
     */
    public boolean isPasswordExpired(UserPassword password) {
        long expiration = password.getDateCreated().getTime() + DEFAULT_PASSWORD_EXPIRE_MS;

        if (expiration < System.currentTimeMillis()) {
            return true;
        }

        return false;
    }

    /**
     * Fetches the current encoded password for this user, or null if none is
     * valid.
     */
    public UserPassword getPasswordForUser(User user) {
        List<UserPassword> passwords = (List<UserPassword>) hibernateTemplate.find("from UserPassword where user = ? and active = true order by dateCreated desc", user);

        if (passwords.size() > 0) {
            return passwords.get(0);
        } else {
            return null;
        }
    }

    /**
     * Sets a new password and invalidates the previous ones.
     */
    public void setPasswordForUser(User user, String password) {
        UserPassword userPassword = new UserPassword();

        userPassword.setUser(user);
        userPassword.setPasswordEncoded(passwordEncoder.encode(password));
        userPassword.setPasswordEncodedMD5(DigestUtils.md5Hex(password));
        userPassword.setDateCreated(new Date());
        userPassword.setActive(true);

        for (UserPassword p : user.getPasswords()) {
            p.setActive(false);
            hibernateTemplate.update(p);
        }

        user.getPasswords().add(userPassword);

        hibernateTemplate.save(userPassword);
        hibernateTemplate.update(user);

        log.info("set password for user " + user.getId());
    }

    /**
     * Validates a password and returns a validation error, if any.
     */
    public String validatePassword(User user, String username, String password) {
        if (StringUtils.isEmpty(password) || password.length() < 7) {
            return PASSWORD_TOOSHORT_CODE;
        }

        if (password != null) {
            if (containsNonAsciiChars(password)) {
                return PASSWORD_WEIRDCHARS_CODE;
            }

            if (!StringUtils.containsAny(password, LETTERS) || !StringUtils.containsAny(password, DIGITS)) {
                return PASSWORD_NUMBERANDLETTER_CODE;
            }

            if (!StringUtils.containsAny(password, "ABCDEFGHIJKLMNOPQRSTUVWXYZ")) {
                return PASSWORD_UPPERCASE_CODE;
            }
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
    @SuppressWarnings("unchecked")
    private boolean lastFourPasswordsContains(User user, String password) {
        String passwordEncoded = passwordEncoder.encode(password);
        List<UserPassword> passwords = (List<UserPassword>) hibernateTemplate.find("from UserPassword where user = ? order by dateCreated desc", user);

        for (int i = 0; i < passwords.size() && i < 4; i++) {
            if (passwords.get(i).getPasswordEncoded().equals(passwordEncoded)) {
                return true;
            }
        }

        return false;
    }

    private boolean containsNonAsciiChars(String input) {
        boolean rtn = false;

        if (input != null) {
            for (int i = 0; i < input.length() && rtn == false; i++) {
                if (input.charAt(i) > 255) {
                    rtn = true;
                }
            }
        }

        return rtn;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
