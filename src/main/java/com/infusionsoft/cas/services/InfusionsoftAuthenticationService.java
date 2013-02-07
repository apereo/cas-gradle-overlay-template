package com.infusionsoft.cas.services;

import com.infusionsoft.cas.auth.InfusionsoftCredentials;
import com.infusionsoft.cas.auth.LetMeInCredentials;
import com.infusionsoft.cas.exceptions.CASMappingException;
import com.infusionsoft.cas.exceptions.UsernameTakenException;
import com.infusionsoft.cas.types.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Service for anything related to authentication. This includes authentication of CAS users, lock-out-periods, and
 * outbound authentication requests to external apps.
 */
public class InfusionsoftAuthenticationService {
    private static final Logger log = Logger.getLogger(InfusionsoftAuthenticationService.class);

    private static final long LOCK_PERIOD_MS = 1800000; // 30 minutes
    private static final int LOCK_ATTEMPTS = 5; // how many tries before locked

    private CentralAuthenticationService centralAuthenticationService;
    private InfusionsoftDataService infusionsoftDataService;
    private TicketRegistry ticketRegistry;
    private HibernateTemplate hibernateTemplate;
    private String serverPrefix;
    private String crmProtocol;
    private String crmDomain;
    private String crmPort;
    private String crmVendorKey;
    private String customerHubDomain;
    private String communityDomain;
    private String marketplaceDomain;
    private String marketplaceLoginUrl;
    private String forumBase;
    private String forumApiKey;
    private String migrationDateString;

    /**
     * Builds a URL for redirecting users to an app.
     */
    public String buildAppUrl(String appType, String appName) {
        if (appType.equals(AppType.CRM)) {
            return crmProtocol + "://" + appName + "." + crmDomain + ":" + crmPort;
        } else if (appType.equals(AppType.COMMUNITY)) {
            return "http://" + communityDomain + "/caslogin.php";
        } else if (appType.equals(AppType.CUSTOMERHUB)) {
            return "https://" + appName + "." + customerHubDomain;
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
        LoginAttempt attempt = new LoginAttempt();

        attempt.setUsername(credentials.getUsername());
        attempt.setDateAttempted(new Date());
        attempt.setSuccess(success);

        hibernateTemplate.save(attempt);
    }

    /**
     * Returns all login attempts within the last 30 days, for a particular username.
     */
    public List<LoginAttempt> getRecentLoginAttempts(InfusionsoftCredentials credentials) {
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - (86400000L * 30));

        return hibernateTemplate.find("from LoginAttempt a where a.username = ? and a.dateAttempted > ? order by a.dateAttempted desc", credentials.getUsername(), thirtyDaysAgo);
    }

    /**
     * Tells how many consecutive failed login attempts there are for a particular user name.
     */
    public int countConsecutiveFailedLogins(InfusionsoftCredentials credentials) {
        List<LoginAttempt> attempts = getRecentLoginAttempts(credentials);
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

        log.debug("user " + credentials.getUsername() + " has " + attempts.size() + " recent login attempts and " + failures + " consecutive failures");

        return failures;
    }

    /**
     * Returns the most recent failed login attempt.
     */
    public LoginAttempt getMostRecentFailedLogin(InfusionsoftCredentials credentials) {
        List<LoginAttempt> attempts = getRecentLoginAttempts(credentials);

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
    public boolean isAccountLocked(InfusionsoftCredentials credentials) {
        if (countConsecutiveFailedLogins(credentials) > LOCK_ATTEMPTS) {
            LoginAttempt mostRecent = getMostRecentFailedLogin(credentials);
            Date lockPeriodStart = new Date(System.currentTimeMillis() - LOCK_PERIOD_MS);

            if (mostRecent.getDateAttempted().after(lockPeriodStart)) {
                log.info("username " + credentials.getUsername() + " is locked due to more than " + LOCK_ATTEMPTS + " failures in the last " + LOCK_PERIOD_MS + " milliseconds");

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
            valid = verifyCRMCredentials(appName, appUsername, appPassword);
        } else if (StringUtils.equals(appType, AppType.COMMUNITY)) {
            valid = verifyCommunityCredentials(appUsername, appPassword) != null;
        } else if (StringUtils.equals(appType, AppType.CUSTOMERHUB)) {
            // TODO - add verification for CustomerHub
            log.error("we don't know how to verify credentials for CustomerHub!");

            valid = false;
        } else {
            log.warn("we don't know how to verify credentials for app type " + appType);
        }

        return valid;
    }

    /**
     * Verifies a username and password against a CRM app.
     */
    private boolean verifyCRMCredentials(String appName, String appUsername, String appPassword) {
        try {
            XmlRpcClient client = new XmlRpcClient(buildAppUrl(AppType.CRM, appName) + "/api/xmlrpc");
            Vector<String> params = new Vector<String>();

            log.debug("attempting to verify crm credentials at url " + client.getURL() + " with vendor key " + crmVendorKey);

            params.add(crmVendorKey);
            params.add(appUsername);
            params.add(DigestUtils.md5Hex(appPassword));

            Object response = client.execute("DataService.getTemporaryKey", params);

            if (response != null) {
                log.info("getTemporaryKey returned a response " + response + " of type " + response.getClass());

                return true;
            } else {
                log.warn("unable to verify credentials! no temp key was returned for this username and password");
            }
        } catch (MalformedURLException e) {
            log.error("couldn't verify app credentials: xml-rpc url is invalid!", e);
        } catch (IOException e) {
            log.warn("web service call failed", e);
        } catch (XmlRpcException e) {
            log.info("app credentials are invalid", e);
        }

        return false;
    }

    /**
     * Verifies a username and password with the Infusionsoft Community. Returns a String of the user's userid if
     * valid.
     */
    public String verifyCommunityCredentials(String appUsername, String appPassword) {
        String userId = null;

        try {
            RestTemplate restTemplate = new RestTemplate();

            log.info("preparing REST call to " + forumBase);

            String md5password = DigestUtils.md5Hex(appPassword);
            String result = restTemplate.getForObject("{base}/rest.php/user/isvaliduser?key={apiKey}&username={appUsername}&md5password={md5password}", String.class, forumBase, forumApiKey, appUsername, md5password);

            log.debug("REST response from community: " + result);

            JSONObject returnValue = (JSONObject) JSONValue.parse(result);
            Boolean returnValid = (Boolean) returnValue.get("valid");

            userId = (String) returnValue.get("userid");

            if (returnValid == null || !returnValid.booleanValue()) {
                log.warn("community user credentials for " + appUsername + " are invalid");
            }
        } catch (Exception e) {
            log.error("couldn't validate user credentials in community", e);
        }

        return userId;
    }

    /**
     * Calls out to the Community web service to try to create a new user. This is for users who create their Community
     * profile through CAS.
     */
    public UserAccount registerCommunityUserAccount(User user, CommunityAccountDetails details) throws RestClientException, UsernameTakenException, CASMappingException {
        RestTemplate restTemplate = new RestTemplate();

        log.info("preparing REST call to " + forumBase);

        // TODO - retarded Restler won't read our params from the POST body, so we put them on the query string for now
        String username = details.getDisplayName();
        String email = StringUtils.isNotEmpty(details.getNotificationEmailAddress()) ? details.getNotificationEmailAddress() : user.getUsername();
        String response = restTemplate.postForObject("{base}/rest.php/user/addnewuser?key={apiKey}&username={username}&email={email}&experience={experience}&twitter={twitter}&timezone={timezone}", "", String.class, forumBase, forumApiKey, username, email, details.getInfusionsoftExperience(), details.getTwitterHandle(), details.getTimeZone());
        JSONObject responseJson = (JSONObject) JSONValue.parse(response);
        Boolean hasError = (Boolean) responseJson.get("error");
        String userId = String.valueOf(responseJson.get("userId"));

        if (hasError) {
            throw new UsernameTakenException("the display name " + details.getDisplayName() + " is already taken");
        }

        UserAccount account = infusionsoftDataService.associateAccountToUser(user, AppType.COMMUNITY, "Infusionsoft Community", userId);

        details.setUserAccount(account);
        hibernateTemplate.save(details);

        log.info("created community account details " + details.getId() + " for account " + account.getId());

        return account;
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

    public void setForumBase(String forumBase) {
        this.forumBase = forumBase;
    }

    public void setForumApiKey(String forumApiKey) {
        this.forumApiKey = forumApiKey;
    }

    public String getServerPrefix() {
        return serverPrefix;
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

    public void setInfusionsoftDataService(InfusionsoftDataService infusionsoftDataService) {
        this.infusionsoftDataService = infusionsoftDataService;
    }

    public void setCrmVendorKey(String crmVendorKey) {
        this.crmVendorKey = crmVendorKey;
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
}
