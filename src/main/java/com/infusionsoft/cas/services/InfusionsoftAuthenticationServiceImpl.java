package com.infusionsoft.cas.services;

import com.infusionsoft.cas.auth.LoginResult;
import com.infusionsoft.cas.dao.LoginAttemptDAO;
import com.infusionsoft.cas.domain.*;
import com.infusionsoft.cas.exceptions.AppCredentialsExpiredException;
import com.infusionsoft.cas.exceptions.AppCredentialsInvalidException;
import com.infusionsoft.cas.support.AppHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationManager;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.ticket.support.TicketGrantingTicketExpirationPolicy;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.base.BaseSingleFieldPeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class InfusionsoftAuthenticationServiceImpl implements InfusionsoftAuthenticationService {
    private static final Logger log = Logger.getLogger(InfusionsoftAuthenticationServiceImpl.class);

    private static final BaseSingleFieldPeriod lockoutTimePeriod = Minutes.minutes(30);
    private static final int LOCK_ATTEMPTS = 5; // how many tries before locked

    @Autowired
    CustomerHubService customerHubService;

    @Autowired
    CommunityService communityService;

    @Autowired
    CrmService crmService;

    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    @Autowired
    @Qualifier("warnCookieGenerator")
    CookieRetrievingCookieGenerator warnCookieGenerator;

    @Autowired
    TicketRegistry ticketRegistry;

    @Autowired
    LoginAttemptDAO loginAttemptDAO;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    PasswordService passwordService;

    @Autowired
    AppHelper appHelper;

    @Autowired
    TicketGrantingTicketExpirationPolicy ticketGrantingTicketExpirationPolicy;

    @Value("${server.prefix}")
    String serverPrefix;

    @Value("${infusionsoft.crm.protocol}")
    String crmProtocol;

    @Value("${infusionsoft.crm.domain}")
    String crmDomain;

    @Value("${infusionsoft.crm.port}")
    String crmPort;

    @Value("${infusionsoft.customerhub.domain}")
    String customerHubDomain;

    @Value("${infusionsoft.community.domain}")
    String communityDomain;

    @Value("${infusionsoft.marketplace.domain}")
    String marketplaceDomain;

    @Value("${infusionsoft.marketplace.loginurl}")
    String marketplaceLoginUrl;

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

            if (url.toString().startsWith(serverPrefix)) {
                log.info("it's us");
            } else if (host.equals(communityDomain)) {
                appName = "community";
            } else if (host.equals(marketplaceDomain)) {
                appName = "marketplace";
            } else if (host.endsWith(crmDomain)) {
                appName = host.replace("." + crmDomain, "");
            } else if (host.endsWith(customerHubDomain)) {
                appName = host.replace("." + customerHubDomain, "");
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
    public String guessAppType(String url) throws MalformedURLException {
        return StringUtils.isNotEmpty(url) ? guessAppType(new URL(url)) : null;
    }

    /**
     * Guesses an app type from a URL, or null if there isn't one to be found.
     */
    @Override
    public String guessAppType(URL url) {
        String appType = null;
        if (url != null && url.getHost() != null) {
            String host = url.getHost().toLowerCase();

            if (url.toString().startsWith(serverPrefix)) {
                appType = AppType.CAS;
            } else if (host.equals(communityDomain)) {
                appType = AppType.COMMUNITY;
            } else if (host.equals(marketplaceDomain)) {
                appType = AppType.MARKETPLACE;
            } else if (host.endsWith(crmDomain)) {
                appType = AppType.CRM;
            } else if (host.endsWith(customerHubDomain)) {
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
        return attemptLoginAndLogAttempts(username, null, md5password);
    }

    @Override
    public LoginResult attemptLogin(String username, String password) {
        log.debug("Trying to authenticate " + username + " with password");
        return attemptLoginAndLogAttempts(username, password, null);
    }

    private LoginResult attemptLoginAndLogAttempts(String username, String password, String md5password) {
        LoginResult loginResult = attemptLoginInternal(username, password, md5password);

        switch (loginResult.getLoginStatus()) {
            case Success:
                log.info("Authenticated CAS user " + username);
                recordLoginAttempt(username, true);
                break;

            case PasswordExpired:
                log.info("Authenticated CAS user " + username + " with expired password");
                recordLoginAttempt(username, true);
                break;

            case AccountLocked:
            case BadPassword:
            case DisabledUser:
            case NoSuchUser:
                recordLoginAttempt(username, false);
                break;

            default:
                throw new IllegalStateException("Unknown value for loginResult: " + loginResult);
        }

        return loginResult;
    }

    private LoginResult attemptLoginInternal(String username, String password, String md5password) {
        LoginResult retVal;

        User user = userService.loadUser(username);
        if (user == null) {
            retVal = LoginResult.NoSuchUser();
        } else {
            if (!user.isEnabled()) {
                retVal = LoginResult.DisabledUser(user);
            } else {
                UserPassword userPassword = passwordService.getPasswordForUser(user);

                if (userPassword == null) {
                    retVal = LoginResult.BadPassword(user);
                } else {
                    if (StringUtils.isNotEmpty(password)) {
                        if (!passwordService.passwordsMatch(userPassword, password)) {
                            retVal = LoginResult.BadPassword(user);
                        } else {
                            retVal = LoginResult.Success(user);
                        }
                    } else if (StringUtils.isNotEmpty(md5password)) {
                        if (!passwordService.md5PasswordsMatch(userPassword, md5password)) {
                            retVal = LoginResult.BadPassword(user);
                        } else {
                            retVal = LoginResult.Success(user);
                        }
                    } else {
                        retVal = LoginResult.BadPassword(user);
                    }
                }
            }
        }

        if (retVal.getLoginStatus() == LoginResult.LoginStatus.Success) {
            if (passwordService.isPasswordExpired(user)) {
                retVal = LoginResult.PasswordExpired(user);
            }
        } else {
            if (isAccountLocked(username)) {
                retVal = LoginResult.AccountLocked(user);
            }
        }

        return retVal;
    }

    /**
     * Records a login attempt, and whether it was successful or not. This is used for account locking
     * in the case of too many failures.
     */
    private void recordLoginAttempt(String username, boolean success) {
        LoginAttempt attempt = new LoginAttempt();

        attempt.setUsername(username);
        attempt.setDateAttempted(new Date());
        attempt.setSuccess(success);

        loginAttemptDAO.save(attempt);
    }

    /**
     * Returns all login attempts within the X days/minutes/seconds, for a particular username.
     * Of course, these may have been cleared out by the garbage man Quartz job.
     */
    @Override
    public List<LoginAttempt> getRecentLoginAttempts(String username, BaseSingleFieldPeriod baseSingleFieldPeriod) {
        DateTime dateTime = new DateTime().minus(baseSingleFieldPeriod);

        return loginAttemptDAO.findByUsernameAndDateAttemptedGreaterThanOrderByDateAttemptedDesc(username, dateTime.toDate());
    }

    /**
     * Tells how many consecutive failed login attempts there are for a particular user name.
     */
    @Override
    public int countConsecutiveFailedLogins(String username) {
        List<LoginAttempt> attempts = getRecentLoginAttempts(username, lockoutTimePeriod);
        int failures = 0;

        log.debug("recent login attempts: " + attempts.size());

        for (LoginAttempt loginAttempt : attempts) {
            if (loginAttempt.isSuccess()) {
                break;
            } else {
                failures++;
            }
        }

        log.debug("user " + username + " has " + attempts.size() + " recent login attempts and " + failures + " consecutive failures");

        return failures;
    }

    /**
     * Returns the most recent failed login attempt.
     */
    @Override
    public LoginAttempt getMostRecentFailedLogin(String username) {
        LoginAttempt retVal = null;

        List<LoginAttempt> loginAttempts = loginAttemptDAO.findByUsernameAndSuccessFalseOrderByDateAttemptedDesc(username);

        if (loginAttempts != null && !loginAttempts.isEmpty()) {
            retVal = loginAttempts.get(0);
        }

        return retVal;
    }

    /**
     * Checks if an account is locked due to too many login failures.
     */
    @Override
    public boolean isAccountLocked(String username) {

        if (countConsecutiveFailedLogins(username) > LOCK_ATTEMPTS) {
            LoginAttempt mostRecent = getMostRecentFailedLogin(username);
            DateTime lockPeriodStart = new DateTime().minus(lockoutTimePeriod);

            if (mostRecent.getDateAttempted().after(lockPeriodStart.toDate())) {
                log.info("username " + username + " is locked due to more than " + LOCK_ATTEMPTS + " failures in the last " + lockoutTimePeriod.toString() + " milliseconds");

                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * Checks whether a user is associated to an app at a particular URL. Everyone is automatically associated with
     * the marketplace.
     */
    @Override
    public boolean isAppAssociated(User user, URL url) {
        if (guessAppType(url).equals(AppType.MARKETPLACE)) {
            return true;
        }

        for (UserAccount account : user.getAccounts()) {
            try {
                String accountAppUrl = appHelper.buildAppUrl(account.getAppType(), account.getAppName());
                URL appUrl = new URL(accountAppUrl);

                log.debug("checking if host " + appUrl.getHost().toLowerCase() + " matches URL " + appUrl);

                if (appUrl.getHost().toLowerCase().equals(url.getHost().toLowerCase())) {
                    return true;
                }
            } catch (Exception e) {
                log.error("unexpected exception constructing app url", e);
            }
        }


        return false;
    }


    /**
     * Checks with an app whether a user's legacy credentials are correct. This should be done before we allow them to
     * link that account to their CAS account. Throws an exception if the credentials are invalid, expired, etc.
     */
    @Override
    public void verifyAppCredentials(String appType, String appName, String appUsername, String appPassword) throws AppCredentialsInvalidException, AppCredentialsExpiredException {
        if (StringUtils.equals(appType, AppType.CRM)) {
            crmService.authenticateUser(appName, appUsername, appPassword);
        } else if (StringUtils.equals(appType, AppType.COMMUNITY)) {
            if (communityService.authenticateUser(appUsername, appPassword) == null) {
                throw new AppCredentialsInvalidException("community credentials are invalid or could not be verified");
            }
        } else if (StringUtils.equals(appType, AppType.CUSTOMERHUB)) {
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

        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("CASTGC")) {
                log.debug("found a valid CASTGC cookie with value " + cookie.getValue());

                Ticket ticket = ticketRegistry.getTicket(cookie.getValue());
                TicketGrantingTicket tgt = null;

                if (ticket == null) {
                    log.warn("found a CASTGC cookie, but it doesn't match any known ticket!");
                } else if (ticket instanceof TicketGrantingTicket) {
                    tgt = (TicketGrantingTicket) ticket;
                } else {
                    tgt = ticket.getGrantingTicket();
                }

                if (tgt != null) {
                    Principal principal = tgt.getAuthentication().getPrincipal();
                    User user = userService.loadUser(principal.getId());

                    if (user != null) {
                        retVal = user;

                        log.info("resolved user id=" + retVal.getId() + " for ticket " + tgt);
                    } else {
                        log.warn("couldn't find a user for ticket " + tgt);
                    }
                }
            }
        }

        return retVal;
    }

    /**
     * Tells if a user is currently associated to a given app.
     */
    @Override
    public boolean isUserAssociated(User user, String appType, String appName) {
        for (UserAccount account : user.getAccounts()) {
            if (account.getAppType().equals(appType) && account.getAppName().equals(appName)) {
                return true;
            }
        }

        return false;
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

    @Override
    public void unlockUser(String username) {
        recordLoginAttempt(username, true);
    }

    @Override
    public void autoLogin(String ticketGrantingTicketId, String username, String password) throws AuthenticationException {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials();
        credentials.setUsername(username);
        credentials.setPassword(password);

//        if(ticketGrantingTicketId != null) {
//            ticketRegistry.deleteTicket()
//        }

        final Authentication authentication = this.authenticationManager.authenticate(credentials);

        final TicketGrantingTicket ticketGrantingTicket = new TicketGrantingTicketImpl(ticketGrantingTicketId, authentication, ticketGrantingTicketExpirationPolicy);

        this.ticketRegistry.addTicket(ticketGrantingTicket);

    }
}
