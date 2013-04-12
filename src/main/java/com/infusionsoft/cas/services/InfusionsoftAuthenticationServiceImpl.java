package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.LoginAttemptDAO;
import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.LoginAttempt;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.AppCredentialsExpiredException;
import com.infusionsoft.cas.exceptions.AppCredentialsInvalidException;
import com.infusionsoft.cas.support.AppHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    private static final long LOCK_PERIOD_MS = 1800000; // 30 minutes
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
    UserService userService;

    @Autowired
    AppHelper appHelper;

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

    /**
     * Records a login attempt, and whether it was successful or not. This is used for account locking
     * in the case of too many failures.
     */
    @Override
    public void recordLoginAttempt(String username, boolean success) {
        LoginAttempt attempt = new LoginAttempt();

        attempt.setUsername(username);
        attempt.setDateAttempted(new Date());
        attempt.setSuccess(success);

        loginAttemptDAO.save(attempt);
    }

    /**
     * Returns all login attempts within the last 30 days, for a particular username.
     * Of course, these may have been cleared out by the garbage man Quartz job.
     */
    @Override
    public List<LoginAttempt> getRecentLoginAttempts(String username) {
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - (86400000L * 30));

        return loginAttemptDAO.findByUsernameGreaterThanDateAttempted(username, thirtyDaysAgo);
    }

    /**
     * Tells how many consecutive failed login attempts there are for a particular user name.
     */
    @Override
    public int countConsecutiveFailedLogins(String username) {
        List<LoginAttempt> attempts = getRecentLoginAttempts(username);
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
        List<LoginAttempt> attempts = getRecentLoginAttempts(username);

        for (LoginAttempt attempt : attempts) {
            if (!attempt.isSuccess()) {
                return attempt;
            }
        }

        return null;
    }

    /**
     * Checks if an account is locked due to too many login failures.
     */
    @Override
    public boolean isAccountLocked(String username) {
        if (countConsecutiveFailedLogins(username) > LOCK_ATTEMPTS) {
            LoginAttempt mostRecent = getMostRecentFailedLogin(username);
            Date lockPeriodStart = new Date(System.currentTimeMillis() - LOCK_PERIOD_MS);

            if (mostRecent.getDateAttempted().after(lockPeriodStart)) {
                log.info("username " + username + " is locked due to more than " + LOCK_ATTEMPTS + " failures in the last " + LOCK_PERIOD_MS + " milliseconds");

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

//    /**
//     * Creates (or updates) a CAS ticket granting ticket. Sometimes this needs to be called after an attributes change,
//     * to give the user a new ticket and refresh attributes. It should only be called when the user is already
//     * authenticated and trusted, since it automatically creates a new session without validating the password.
//     */
//    //TODO: Removed from trying to get app up
//    @Override
//    public void createTicketGrantingTicket(String username, HttpServletRequest request, HttpServletResponse response) throws TicketException {
//        LetMeInCredentials credentials = new LetMeInCredentials();
//
//        credentials.setUsername(username);
//        credentials.setPassword("bogus");
//
//
//        String ticketGrantingTicket = centralAuthenticationService.createTicketGrantingTicket(credentials);
//        String contextPath = request.getContextPath();
//
//        if (!contextPath.endsWith("/")) {
//            contextPath = contextPath + "/";
//        }
//
//        Cookie cookie = new Cookie("CASTGC", ticketGrantingTicket);
//        cookie.setPath(contextPath);
//        cookie.setSecure(true);
//
//        response.addCookie(cookie);
//
//        log.info("set cookie CASTGC=" + ticketGrantingTicket);
//    }

//    /**
//     * Gets the ticket granting ticket associated with the current request.
//     */
//    @Override
//    public String getTicketGrantingTicketId(HttpServletRequest request) {
//        return ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
//    }

//    /**
//     * Destroys the ticket granting ticket associated with the current request.
//     */
//    @Override
//    public void destroyTicketGrantingTicket(HttpServletRequest request, HttpServletResponse response) {
//        String ticketGrantingTicketId = getTicketGrantingTicketId(request);
//
//        //TODO: Removed from trying to get app up
//        centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicketId);
//        ticketGrantingTicketCookieGenerator.removeCookie(response);
//        warnCookieGenerator.removeCookie(response);
//    }

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


}
