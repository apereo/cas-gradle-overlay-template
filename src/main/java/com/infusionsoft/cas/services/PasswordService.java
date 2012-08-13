package com.infusionsoft.cas.services;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.testng.log4testng.Logger;

import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserPassword;

public class PasswordService {
	private static final Logger log = Logger.getLogger(PasswordService.class);

	private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
	private static final String LETTERS = UPPERCASE_LETTERS + LOWERCASE_LETTERS;
	private static final String DIGITS = "0123456789";

	private static final int DEFAULT_PASSWORD_EXPIRE_DAYS = 90;
	private static final int DEFAULT_PASSWORD_EXPIRE_MS = DEFAULT_PASSWORD_EXPIRE_DAYS * 3600000 * 24;

	public static final String ROLE_EXPIRED_PASSWORD = "ROLE_EXPIRED_PASSWORD";

	public static final String PASSWORD_TOOSHORT_CODE = "password.tooshort";
	public static final String PASSWORD_WEIRDCHARS_CODE = "password.weirdcharacters";
	public static final String PASSWORD_NUMBERANDLETTER_CODE = "password.numberandletter";
	public static final String PASSWORD_UPPERCASE_CODE = "password.uppercase";
	public static final String PASSWORD_CONTAINUSERNAME_CODE = "password.nousername";
	public static final String PASSWORD_CANTMATCH_CODE = "password.cantmatch";

	private HibernateTemplate hibernateTemplate;
	private PasswordEncoder passwordEncoder;

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

		if (expiration > System.currentTimeMillis()) {
			return true;
		}

		return false;
	}

	/**
	 * Fetches the current encoded password for this user, or null if none is
	 * valid.
	 */
	@SuppressWarnings("unchecked")
	public UserPassword getPasswordForUser(User user) {
		List<UserPassword> passwords = (List<UserPassword>) hibernateTemplate.find("from Password where user = ? and active = true order by dateCreated desc", user);

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
		userPassword.setDateCreated(new Date());
		userPassword.setActive(true);

		for (UserPassword p : user.getPasswords()) {
			p.setActive(false);
			hibernateTemplate.update(p);
		}

		user.getPasswords().add(userPassword);

		hibernateTemplate.save(userPassword);
		hibernateTemplate.update(user);
	}

	/**
	 * Validates a password and returns any validation errors.
	 */
	public Set<String> validatePassword(User user, String username, String password) {
		Set<String> errors = new HashSet<String>();

		if (StringUtils.isEmpty(password) || password.length() < 7) {
			errors.add(PASSWORD_TOOSHORT_CODE);
		}

		if (password != null) {
			if (containsNonAsciiChars(password)) {
				errors.add(PASSWORD_WEIRDCHARS_CODE);
			}

			if (!StringUtils.containsAny(password, LETTERS) || !StringUtils.contains(password, DIGITS)) {
				errors.add(PASSWORD_NUMBERANDLETTER_CODE);
			}

			if (!StringUtils.containsAny(password, "ABCDEFGHIJKLMNOPQRSTUVWXYZ")) {
				errors.add(PASSWORD_UPPERCASE_CODE);
			}
		}

		if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
			String usernameLower = username.toLowerCase();
			String passwordLower = password.toLowerCase();

			if (passwordLower.equals(usernameLower) || passwordLower.contains(usernameLower)) {
				errors.add(PASSWORD_CONTAINUSERNAME_CODE);
			}
		}

		if (lastFourPasswordsContains(user, password)) {
			errors.add(PASSWORD_CANTMATCH_CODE);
		}

		return errors;
	}

	/**
	 * Checks if a password matches any of the last four passwords.
	 */
	@SuppressWarnings("unchecked")
	private boolean lastFourPasswordsContains(User user, String password) {
		String passwordEncoded = passwordEncoder.encode(password);
		List<UserPassword> passwords = (List<UserPassword>) hibernateTemplate.find("from Password where user = ? order by dateCreated desc", user);

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
}
