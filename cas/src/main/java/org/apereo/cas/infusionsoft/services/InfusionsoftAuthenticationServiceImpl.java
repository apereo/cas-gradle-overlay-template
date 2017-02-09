package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.authentication.LoginResult;
import org.apereo.cas.infusionsoft.config.properties.CustomerHubConfigurationProperties;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.config.properties.MarketplaceConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.LoginAttemptDAO;
import org.apereo.cas.infusionsoft.domain.*;
import org.apereo.cas.infusionsoft.exceptions.AppCredentialsExpiredException;
import org.apereo.cas.infusionsoft.exceptions.AppCredentialsInvalidException;
import org.apereo.cas.infusionsoft.web.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.base.BaseSingleFieldPeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Service for anything related to authentication. This includes authentication of CAS users, lock-out-periods, and
 * outbound authentication requests to external apps.
 */
@Service("infusionsoftAuthenticationService")
@Transactional(transactionManager = "transactionManager")
@EnableConfigurationProperties({CasConfigurationProperties.class, CustomerHubConfigurationProperties.class, InfusionsoftConfigurationProperties.class, MarketplaceConfigurationProperties.class})
public class InfusionsoftAuthenticationServiceImpl implements InfusionsoftAuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(InfusionsoftAuthenticationServiceImpl.class);

    private static final Minutes lockoutTimePeriod = Minutes.minutes(30);

    @Autowired
    CasConfigurationProperties casProperties;

    @Autowired
    CustomerHubConfigurationProperties customerHubProperties;

    @Autowired
    InfusionsoftConfigurationProperties infusionsoftProperties;

    @Autowired
    MarketplaceConfigurationProperties marketplaceProperties;

    @Autowired
    private CustomerHubService customerHubService;

    @Autowired
    private TicketRegistry ticketRegistry;

    @Autowired
    private LoginAttemptDAO loginAttemptDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    private CookieRetrievingCookieGenerator tgtCookieGenerator;

    @Value("${infusionsoft.cas.security.questions.force.answer}")
    private boolean forceSecurityQuestion;

    @Value("${infusionsoft.cas.security.questions.number.required}")
    private int securityQuestionRequiredResponseCount;


    /**
     * Guesses an app name from a URL, or null if there isn't one to be found.
     */
    @Override
    public String guessAppName(String url) throws MalformedURLException {
        return StringUtils.isNotEmpty(url) ? guessAppName(new URL(url)) : null;
    }

    /**
     * Guesses an app name from a URL, or null if there isn't one to be found.
     */
    @Override
    public String guessAppName(URL url) {
        String appName = null;

        if (url != null && url.getHost() != null) {
            String host = url.getHost().toLowerCase();

            if (url.toString().startsWith(casProperties.getServer().getPrefix())) {
                log.info("it's us");
            } else if (host.equals(marketplaceProperties.getHost().getDomain())) {
                appName = "marketplace";
            } else if (host.endsWith(infusionsoftProperties.getCrm().getDomain())) {
                appName = host.replace("." + infusionsoftProperties.getCrm().getDomain(), "");
            } else if (host.endsWith(customerHubProperties.getHost().getDomain())) {
                appName = host.replace("." + customerHubProperties.getHost().getDomain(), "");
            } else {
                log.warn("unable to guess app name for url " + url);
            }

            if (appName != null) {
                log.debug("app name for url " + url + " is " + appName);
            }
        }

        return appName;
    }

    /**
     * Guesses an app type from a URL, or null if there isn't one to be found.
     */
    @Override
    public AppType guessAppType(String url) throws MalformedURLException {
        return StringUtils.isNotEmpty(url) ? guessAppType(new URL(url)) : null;
    }

    /**
     * Guesses an app type from a URL, or null if there isn't one to be found.
     */
    @Override
    public AppType guessAppType(URL url) {
        AppType appType = null;

        if (url != null && url.getHost() != null) {
            String host = url.getHost().toLowerCase();

            if (url.toString().startsWith(casProperties.getServer().getPrefix())) {
                appType = AppType.CAS;
            } else if (host.equals(marketplaceProperties.getHost().getDomain())) {
                appType = AppType.MARKETPLACE;
            } else if (host.endsWith(infusionsoftProperties.getCrm().getDomain())) {
                appType = AppType.CRM;
            } else if (host.endsWith(customerHubProperties.getHost().getDomain())) {
                appType = AppType.CUSTOMERHUB;
            } else {
                log.warn("unable to guess app type for url " + url);
            }

            if (appType != null) {
                log.debug("app type for url " + url + " is " + appType);
            }
        }

        return appType;
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
            // Already logged elsewhere
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
                if (loginAttemptStatus == null || loginAttemptStatus == LoginAttemptStatus.AccountLocked) {
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

    /**
     * Checks with an app whether a user's legacy credentials are correct. This should be done before we allow them to
     * link that account to their CAS account. Throws an exception if the credentials are invalid, expired, etc.
     */
    @Override
    public void verifyAppCredentials(AppType appType, String appName, String appUsername, String appPassword) throws AppCredentialsInvalidException, AppCredentialsExpiredException {
        if (AppType.CUSTOMERHUB.equals(appType)) {
            if (!customerHubService.authenticateUser(appName, appUsername, appPassword)) {
                throw new AppCredentialsInvalidException("customerhub credentials are invalid or could not be verified");
            }
        } else {
            throw new AppCredentialsInvalidException("we don't know how to verify credentials for app type " + appType);
        }
    }

    /**
     * Looks at the CAS cookies to determine the current user.
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        User retVal = null;
        TicketGrantingTicket tgt = getTicketGrantingTicket(request);
        Principal principal = tgt.getAuthentication().getPrincipal();
        User user = userService.loadUser(principal.getId());

        if (user != null) {
            retVal = user;

            log.info("resolved user id=" + retVal.getId() + " for ticket " + tgt);
        } else {
            log.warn("couldn't find a user for ticket " + tgt);
        }
        return retVal;
    }

    @Override
    public TicketGrantingTicket getTicketGrantingTicket(HttpServletRequest request) {
        TicketGrantingTicket tgt = null;

        if (request.getCookies() == null) {
            return null;
        }

        String tgtCookieName = tgtCookieGenerator.getCookieName();
        Cookie cookie = CookieUtil.extractCookie(request, tgtCookieName);
        if (cookie != null) {
            log.debug("found a valid " + tgtCookieName + " cookie with value " + cookie.getValue());

            Ticket ticket = ticketRegistry.getTicket(cookie.getValue());

            if (ticket == null) {
                log.warn("found a " + tgtCookieName + " cookie, but it doesn't match any known ticket!");
            } else if (ticket instanceof TicketGrantingTicket) {
                tgt = (TicketGrantingTicket) ticket;
            } else {
                tgt = ticket.getGrantingTicket();
            }

            if (tgt != null && tgt.isExpired()) {
                tgt = null;
            }
        }

        return tgt;
    }

    /**
     * Tells if a user has a community account associated.
     */
    @Override
    public boolean hasCommunityAccount(User user) {
        for (UserAccount account : user.getAccounts()) {
            if (account.getAppType().equals(AppType.COMMUNITY) && !account.isDisabled()) {
                return true;
            }
        }

        return false;
    }

    /*
        TODO: use CAS auditing, something like this:
        @Audit(
                action="UNLOCK_USER",
                actionResolverName="UNLOCK_USER_RESOLVER",
                resourceResolverName="UNLOCK_USER_RESOURCE_RESOLVER")
    */
    @Override
    public void unlockUser(String username) {
        recordLoginAttempt(LoginAttemptStatus.UnlockedByAdmin, username);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) (authentication == null ? null : authentication.getPrincipal());
        if (user == null) {
            // This happens in testing, but it's a real problem if it happens in production!
            log.error("Unknown user unlocked username " + username);
        } else {
            // This is a warning because we want to make sure it gets audited in the logs
            log.warn("User " + user.getUsername() + " unlocked username " + username);
        }
    }

    public void completePasswordReset(User user) {
        userService.clearPasswordRecoveryCode(user.getId());
        log.info("Password reset completed by user " + user);
        recordLoginAttempt(LoginAttemptStatus.PasswordReset, user.getUsername());
    }

}
