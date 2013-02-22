package com.infusionsoft.cas.services;

import com.infusionsoft.cas.auth.InfusionsoftCredentials;
import com.infusionsoft.cas.auth.LetMeInCredentials;
import com.infusionsoft.cas.types.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.orm.hibernate3.HibernateTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Service for anything related to authentication. This includes authentication of CAS users, lock-out-periods, and
 * outbound authentication requests to external apps.
 */
public class InfusionsoftAuthenticationService {
    private static final Logger log = Logger.getLogger(InfusionsoftAuthenticationService.class);

    private static final long LOCK_PERIOD_MS = 1800000; // 30 minutes
    private static final int LOCK_ATTEMPTS = 5; // how many tries before locked

    private CentralAuthenticationService centralAuthenticationService;
    private CustomerHubService customerHubService;
    private CommunityService communityService;
    private CrmService crmService;
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;
    private CookieRetrievingCookieGenerator warnCookieGenerator;
    private TicketRegistry ticketRegistry;
    private HibernateTemplate hibernateTemplate;

    private String serverPrefix;
    private String crmProtocol;
    private String crmDomain;
    private String crmPort;
    private String customerHubDomain;
    private String communityDomain;
    private String marketplaceDomain;
    private String marketplaceLoginUrl;
    private String migrationDateString;

    /**
     * Builds a URL for redirecting users to an app.
     */
    public String buildAppUrl(String appType, String appName) {
        if (appType.equals(AppType.CRM)) {
            return crmService.buildCrmUrl(appName);
        } else if (appType.equals(AppType.COMMUNITY)) {
            return communityService.buildUrl();
        } else if (appType.equals(AppType.CUSTOMERHUB)) {
            return customerHubService.buildUrl(appName);
        } else {
            log.warn("app url requested for unknown app type: " + appType);

            return "/";
        }
    }

    /**
     * Guesses an app name from a URL, or null if there isn't one to be found.
     */
    public String guessAppName(URL url) {
        String appName = null;
        String host = url.getHost().toLowerCase();

        if (url.toString().startsWith(serverPrefix)) {
            // it's us!
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

        return appName;
    }

    /**
     * Guesses an app type from a URL, or null if there isn't one to be found.
     */
    public String guessAppType(URL url) {
        String appType = null;
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

        return appType;
    }

    /**
     * Records a login attempt, and whether it was successful or not. This is used for account locking
     * in the case of too many failures.
     */
    public void recordLoginAttempt(InfusionsoftCredentials credentials, boolean success) {
        recordLoginAttempt(credentials.getUsername(), success);
    }

    /**
     * Records a login attempt, and whether it was successful or not. This is used for account locking
     * in the case of too many failures.
     */
    public void recordLoginAttempt(String username, boolean success) {
        LoginAttempt attempt = new LoginAttempt();

        attempt.setUsername(username);
        attempt.setDateAttempted(new Date());
        attempt.setSuccess(success);

        hibernateTemplate.save(attempt);
    }

    /**
     * Returns all login attempts within the last 30 days, for a particular username.
     * Of course, these may have been cleared out by the garbage man Quartz job.
     */
    public List<LoginAttempt> getRecentLoginAttempts(String username) {
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - (86400000L * 30));

        return hibernateTemplate.find("from LoginAttempt a where a.username = ? and a.dateAttempted > ? order by a.dateAttempted desc", username, thirtyDaysAgo);
    }

    /**
     * Tells how many consecutive failed login attempts there are for a particular user name.
     */
    public int countConsecutiveFailedLogins(String username) {
        List<LoginAttempt> attempts = getRecentLoginAttempts(username);
        int failures = 0;

        log.debug("recent login attempts: " + attempts.size());

        for (int i = 0; i < attempts.size(); i++) {
            LoginAttempt attempt = attempts.get(i);

            if (attempt.isSuccess()) {
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
    public boolean isAppAssociated(User user, URL url) {
        if (guessAppType(url) == AppType.MARKETPLACE) {
            return true;
        }

        for (UserAccount account : user.getAccounts()) {
            try {
                String accountAppUrl = buildAppUrl(account.getAppType(), account.getAppName());
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
     * Checks whether an app has been fully migrated to CAS.
     */
    public boolean isAppMigrated(String appName, String appType) {
        List<MigratedApp> results = hibernateTemplate.find("from MigratedApp a where a.appName = ? and a.appType = ?", appName, appType);
        boolean migrated = results.size() > 0;

        log.debug("has app " + appName + "/" + appType + " been migrated to CAS? " + migrated);

        return migrated;
    }

    /**
     * Checks with an app whether a user's legacy credentials are correct. This should be done before we allow them to
     * link that account to their CAS account.
     */
    public boolean verifyAppCredentials(String appType, String appName, String appUsername, String appPassword) {
        boolean valid = false;

        if (StringUtils.equals(appType, AppType.CRM)) {
            valid = crmService.authenticateUser(appName, appUsername, appPassword);
        } else if (StringUtils.equals(appType, AppType.COMMUNITY)) {
            valid = communityService.authenticateUser(appUsername, appPassword) != null;
        } else if (StringUtils.equals(appType, AppType.CUSTOMERHUB)) {
            valid = customerHubService.authenticateUser(appName, appUsername, appPassword);
        } else {
            log.warn("we don't know how to verify credentials for app type " + appType);
        }

        return valid;
    }

    /**
     * Creates (or updates) a CAS ticket granting ticket. Sometimes this needs to be called after an attributes change,
     * to give the user a new ticket and refresh attributes. It should only be called when the user is already
     * authenticated and trusted, since it automatically creates a new session without validating the password.
     */
    public void createTicketGrantingTicket(String username, HttpServletRequest request, HttpServletResponse response) throws TicketException {
        LetMeInCredentials credentials = new LetMeInCredentials();

        credentials.setUsername(username);
        credentials.setPassword("bogus");

        String ticketGrantingTicket = centralAuthenticationService.createTicketGrantingTicket(credentials);
        String contextPath = request.getContextPath();

        if (!contextPath.endsWith("/")) {
            contextPath = contextPath + "/";
        }

        Cookie cookie = new Cookie("CASTGC", ticketGrantingTicket);
        cookie.setPath(contextPath);

        response.addCookie(cookie);

        log.info("set cookie CASTGC=" + ticketGrantingTicket);
    }

    /**
     * Gets the ticket granting ticket associated with the current request.
     */
    public String getTicketGrantingTicketId(HttpServletRequest request) {
        return ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
    }

    /**
     * Destroys the ticket granting ticket associated with the current request.
     */
    public void destroyTicketGrantingTicket(HttpServletRequest request, HttpServletResponse response) {
        String ticketGrantingTicketId = getTicketGrantingTicketId(request);

        centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicketId);
        ticketGrantingTicketCookieGenerator.removeCookie(response);
        warnCookieGenerator.removeCookie(response);
    }

    /**
     * Looks at the CAS cookies to determine the current user.
     */
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
                    List<User> users = (List<User>) hibernateTemplate.find("from User user where user.username = ?", principal.getId());

                    if (users.size() > 0) {
                        retVal = (User) users.get(0);

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
    public boolean hasCommunityAccount(User user) {
        for (UserAccount account : user.getAccounts()) {
            if (account.getAppType().equals(AppType.COMMUNITY) && !account.isDisabled()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Builds a JSON string that represents a CAS user and all linked accounts.
     */
    public String buildUserInfoJSON(User user) {
        JSONObject json = new JSONObject();

        json.put("id", user.getId());
        json.put("username", user.getUsername());
        json.put("displayName", user.getFirstName() + " " + user.getLastName());
        json.put("firstName", user.getFirstName());
        json.put("lastName", user.getLastName());

        JSONArray accountsArray = new JSONArray();

        for (UserAccount account : user.getAccounts()) {
            if (!account.isDisabled()) {
                JSONObject accountToAdd = new JSONObject();

                accountToAdd.put("type", account.getAppType());
                accountToAdd.put("appName", account.getAppName());
                accountToAdd.put("userName", account.getAppUsername());
                accountToAdd.put("appAlias", account.getAlias());

                accountsArray.add(accountToAdd);
            }
        }

        json.put("accounts", accountsArray);

        return json.toJSONString();
    }

    public void setCentralAuthenticationService(CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    public void setTicketRegistry(TicketRegistry ticketRegistry) {
        this.ticketRegistry = ticketRegistry;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setServerPrefix(String serverPrefix) {
        this.serverPrefix = serverPrefix;
    }

    public String getCrmProtocol() {
        return crmProtocol;
    }

    public void setCrmProtocol(String crmProtocol) {
        this.crmProtocol = crmProtocol;
    }

    public String getCrmDomain() {
        return crmDomain;
    }

    public void setCrmDomain(String crmDomain) {
        this.crmDomain = crmDomain;
    }

    public String getCrmPort() {
        return crmPort;
    }

    public void setCrmPort(String crmPort) {
        this.crmPort = crmPort;
    }

    public String getCustomerHubDomain() {
        return customerHubDomain;
    }

    public void setCustomerHubDomain(String customerHubDomain) {
        this.customerHubDomain = customerHubDomain;
    }

    public String getMarketplaceDomain() {
        return marketplaceDomain;
    }

    public void setMarketplaceDomain(String marketplaceDomain) {
        this.marketplaceDomain = marketplaceDomain;
    }

    public String getCommunityDomain() {
        return communityDomain;
    }

    public void setCommunityDomain(String communityDomain) {
        this.communityDomain = communityDomain;
    }

    public String getMigrationDateString() {
        return migrationDateString;
    }

    public void setMigrationDateString(String migrationDateString) {
        this.migrationDateString = migrationDateString;
    }

    public void setMarketplaceLoginUrl(String marketplaceLoginUrl) {
        this.marketplaceLoginUrl = marketplaceLoginUrl;
    }

    public String getMarketplaceLoginUrl() {
        return marketplaceLoginUrl;
    }

    public void setCustomerHubService(CustomerHubService customerHubService) {
        this.customerHubService = customerHubService;
    }

    public void setCommunityService(CommunityService communityService) {
        this.communityService = communityService;
    }

    public void setCrmService(CrmService crmService) {
        this.crmService = crmService;
    }

    public void setTicketGrantingTicketCookieGenerator(CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator) {
        this.ticketGrantingTicketCookieGenerator = ticketGrantingTicketCookieGenerator;
    }

    public void setWarnCookieGenerator(CookieRetrievingCookieGenerator warnCookieGenerator) {
        this.warnCookieGenerator = warnCookieGenerator;
    }
}
