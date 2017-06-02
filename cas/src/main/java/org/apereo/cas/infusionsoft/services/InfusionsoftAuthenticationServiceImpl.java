package org.apereo.cas.infusionsoft.services;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.infusionsoft.authentication.LoginResult;
import org.apereo.cas.infusionsoft.dao.LoginAttemptDAO;
import org.apereo.cas.infusionsoft.domain.LoginAttempt;
import org.apereo.cas.infusionsoft.domain.LoginAttemptStatus;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserPassword;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.base.BaseSingleFieldPeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Service for anything related to authentication. This includes authentication of CAS users, lock-out-periods, and
 * outbound authentication requests to external apps.
 */
@Transactional(transactionManager = "transactionManager")
public class InfusionsoftAuthenticationServiceImpl implements InfusionsoftAuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(InfusionsoftAuthenticationServiceImpl.class);
    private static final Minutes lockoutTimePeriod = Minutes.minutes(30);

    private LoginAttemptDAO loginAttemptDAO;
    private UserService userService;
    private PasswordService passwordService;

    public InfusionsoftAuthenticationServiceImpl(LoginAttemptDAO loginAttemptDAO, UserService userService, PasswordService passwordService) {
        this.loginAttemptDAO = loginAttemptDAO;
        this.userService = userService;
        this.passwordService = passwordService;
    }

    @Override
    public LoginResult attemptLoginWithMD5Password(String username, String md5password) {
        log.debug("Trying to authenticate " + username + " with MD5 password");
        return attemptLoginInternal(username, null, md5password);
    }

    @Override
    public LoginResult attemptLogin(String username, String password) {
        log.debug("Trying to authenticate " + username + " with password");
        return attemptLoginInternal(username, password, null);
    }

    private LoginResult attemptLoginInternal(String username, String password, String md5password) {
        LoginResult loginResult = null;
        UserPassword userPassword = null;

        User user = userService.loadUser(username);
        if (user == null) {
            loginResult = LoginResult.NoSuchUser();
        } else if (!user.isEnabled()) {
            loginResult = LoginResult.DisabledUser(user);
        } else if (StringUtils.isNotEmpty(password)) {
            userPassword = passwordService.getMatchingPasswordForUser(user, password);
        } else if (StringUtils.isNotEmpty(md5password)) {
            userPassword = passwordService.getMatchingMD5PasswordForUser(user, md5password);
        } else {
            loginResult = LoginResult.BadPassword(user);
        }

        boolean incrementFailedLoginCount;
        if (loginResult != null) {
            incrementFailedLoginCount = loginResult.getLoginStatus() != LoginAttemptStatus.Success;
        } else {
            if (userPassword == null) {
                loginResult = LoginResult.BadPassword(user);
                incrementFailedLoginCount = true;
            } else if (!userPassword.isActive()) {
                loginResult = LoginResult.OldPassword(user);
                incrementFailedLoginCount = false;  // Bad logins that used an old password don't increment the failure count
            } else {
                loginResult = LoginResult.Success(user);
                incrementFailedLoginCount = false;
            }
        }

        int failedLoginCount = countConsecutiveFailedLogins(username);
        // Account is locked if there are already too many logins or if this is a failed login that pushes it past the limit
        if (isAccountLocked(username, failedLoginCount) || (incrementFailedLoginCount && isAccountLocked(username, failedLoginCount + 1))) {
            loginResult = LoginResult.AccountLocked(user);
            incrementFailedLoginCount = true;
        }

        if (loginResult.getLoginStatus() == LoginAttemptStatus.Success) {
            failedLoginCount = 0;
            if (passwordService.isPasswordExpired(userPassword)) {
                loginResult = LoginResult.PasswordExpired(user);
            }
        } else if (incrementFailedLoginCount) {
            // Add one since the current (failed) login attempt isn't logged yet
            failedLoginCount += 1;
        }

        loginResult.setFailedAttempts(failedLoginCount);

        recordLoginAttempt(loginResult.getLoginStatus(), username);

        return loginResult;
    }

    /**
     * Records a login attempt, and whether it was successful or not. This is used for account locking
     * in the case of too many failures.
     */
    private void recordLoginAttempt(LoginAttemptStatus loginAttemptStatus, String username) {
        LoginAttempt attempt = new LoginAttempt();

        attempt.setUsername(username);
        // TODO: use UTC date here
        attempt.setDateAttempted(new Date());
        attempt.setStatus(loginAttemptStatus);

        loginAttemptDAO.save(attempt);

        if (loginAttemptStatus == LoginAttemptStatus.Success) {
            log.info("Authenticated CAS user " + username);
        } else if (loginAttemptStatus == LoginAttemptStatus.PasswordExpired) {
            log.info("Authenticated CAS user " + username + " with expired password");
        } else if (loginAttemptStatus.isSuccessful()) {
            log.info("Authenticated CAS user " + username + " with password reset ot unlock");
        } else {
            // Write anything to the logs that would help us see where bad logins are coming from
            try {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest httpServletRequest = requestAttributes.getRequest();
                String userAgent = httpServletRequest.getHeader("user-agent");
                String referrer = httpServletRequest.getHeader("referer");
                String remoteAddress = httpServletRequest.getRemoteAddr();
                String requestURI = httpServletRequest.getRequestURI();
                String errorMessage = StringUtils.join("Failed login attempt with status ", loginAttemptStatus, ".\n  username: ", username, "\n  user-agent: ", userAgent, "\n  remoteAddress: ", remoteAddress, "\n  requestURI: ", requestURI, "\n  referrer: ", referrer);
                // Log with an error if it's the one that caused a lockout or already locked out; otherwise info level
                if (loginAttemptStatus == LoginAttemptStatus.AccountLocked) {
                    log.error(errorMessage);
                } else {
                    log.info(errorMessage);
                }
            } catch (Exception e) {
                log.error("Error getting request information when trying to log bad login attempt for user " + username, e);
            }
        }
    }

    /**
     * Returns all login attempts within the X days/minutes/seconds, for a particular username.
     * Of course, these may have been cleared out by the garbage man Quartz job.
     */
    private List<LoginAttempt> getRecentLoginAttempts(String username, BaseSingleFieldPeriod baseSingleFieldPeriod) {
        // TODO: use UTC date here
        DateTime dateTime = new DateTime().minus(baseSingleFieldPeriod);

        return loginAttemptDAO.findByUsernameAndDateAttemptedGreaterThanOrderByDateAttemptedDesc(username, dateTime.toDate());
    }

    /**
     * Tells how many consecutive failed login attempts there are for a particular user name.
     */
    private int countConsecutiveFailedLogins(String username) {
        List<LoginAttempt> attempts = getRecentLoginAttempts(username, lockoutTimePeriod);
        int failures = 0;

        log.debug("recent login attempts: " + attempts.size());

        for (LoginAttempt loginAttempt : attempts) {
            LoginAttemptStatus loginAttemptStatus = loginAttempt.getStatus();
            if (loginAttemptStatus.isSuccessful()) {
                break;
            } else if (loginAttemptStatus != LoginAttemptStatus.OldPassword) {
                failures++;
            }
        }

        log.debug("user " + username + " has " + attempts.size() + " recent login attempts and " + failures + " consecutive failures");

        return failures;
    }

    /**
     * Checks if an account is locked due to too many login failures.
     */
    @Override
    public boolean isAccountLocked(String username) {
        int failedLoginCount = countConsecutiveFailedLogins(username);
        return isAccountLocked(username, failedLoginCount);
    }

    private boolean isAccountLocked(String username, int failedLoginCount) {
        if (failedLoginCount > ALLOWED_LOGIN_ATTEMPTS) {
            log.info("username " + username + " is locked due to more than " + ALLOWED_LOGIN_ATTEMPTS + " failures in the last " + lockoutTimePeriod.getMinutes() + " minutes");
            return true;
        }

        return false;
    }

    public void completePasswordReset(User user) {
        userService.clearPasswordRecoveryCode(user.getId());
        log.info("Password reset completed by user " + user);
        recordLoginAttempt(LoginAttemptStatus.PasswordReset, user.getUsername());
    }

}
