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
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.services.ServiceRegistryDao;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.util.UniqueTicketIdGenerator;
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
 * Utility that handles Spring Security and CAS native authentication tricks.
 */
public class InfusionsoftAuthenticationService {
    private static final Logger log = Logger.getLogger(InfusionsoftAuthenticationService.class);

    private static final long LOCK_PERIOD_MS = 1800000; // 30 minutes
    private static final int LOCK_ATTEMPTS = 5; // how many tries before locked
    private static final long PASSWORD_VALIDITY_MS = 86400000L * 90; // 90 days

    private CentralAuthenticationService centralAuthenticationService;
    private InfusionsoftDataService infusionsoftDataService;
    private ServiceRegistryDao serviceRegistryDao;
    private TicketRegistry ticketRegistry;
    private HibernateTemplate hibernateTemplate;
    private PasswordEncoder passwordEncoder;
    private UniqueTicketIdGenerator ticketIdGenerator;
    private String serverPrefix;
    private String crmProtocol;
    private String crmDomain;
    private String crmPort;
    private String crmVendorKey;
    private String customerHubDomain;
    private String communityDomain;
    private String forumBase;
    private String forumApiKey;
    private String migrationDateString;

    /**
     * Builds a URL for redirecting users to an app.
     */
    public String buildAppUrl(String appType, String appName) {
        // TODO - parameterize protocol, port, etc...
        if (appType.equals("crm")) {
            return crmProtocol + "://" + appName + "." + crmDomain + ":" + crmPort;
        } else if (appType.equals("community")) {
            return "http://" + communityDomain;
        } else if (appType.equals("customerhub")) {
            return "https://" + appName + "." + customerHubDomain;
        } else {
            // TODO
            return "/";
        }
    }

    /**
     * Guesses an app name from a URL, or null if there isn't one to be found.
     */
    public String guessAppName(URL url) {
        String host = url.getHost().toLowerCase();

        log.debug("attempting to guess app name for url " + url);

        if (url.toString().startsWith(serverPrefix)) {
            return null; // it's us!
        } else if (host.endsWith(communityDomain)) {
            return host.replace("." + communityDomain, "");
        } else if (host.endsWith(crmDomain)) {
            return host.replace("." + crmDomain, "");
        } else if (host.endsWith(customerHubDomain)) {
            return host.replace("." + customerHubDomain, "");
        } else {
            return null;
        }
    }

    /**
     * Guesses an app name from a URL, or null if there isn't one to be found.
     */
    public String guessAppType(URL url) {
        String host = url.getHost().toLowerCase();

        log.debug("attempting to guess app type for url " + url);

        if (url.toString().startsWith(serverPrefix)) {
            return null; // it's us!
        } else if (host.endsWith(communityDomain)) {
            return "community";
        } else if (host.endsWith(crmDomain)) {
            return "crm";
        } else if (host.endsWith(customerHubDomain)) {
            return "customerhub";
        } else {
            return null;
        }
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
        Date oneMoonAgo = new Date(System.currentTimeMillis() - (86400000L * 30));

        return hibernateTemplate.find("from LoginAttempt a where a.username = ? and a.dateAttempted > ? order by a.dateAttempted desc", credentials.getUsername(), oneMoonAgo);
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

        log.debug("consecutive failed login attempts: " + failures);

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
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * Checks whether a user is associated to an app at a particular URL. This method actually only compares
     * hostnames, so if there's multiple apps on the same hostname it could be wrong.
     */
    public boolean isAppAssociated(User user, URL url) {
        for (UserAccount account : user.getAccounts()) {
            try {
                URL appUrl = new URL(buildAppUrl(account.getAppType(), account.getAppName()));

                if (url.getHost().toLowerCase().equals(url.getHost().toLowerCase())) {
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

        return results.size() > 0;
    }

    /**
     * Checks the legacy credentials of an app.
     */
    public boolean verifyAppCredentials(String appType, String appName, String appUsername, String appPassword) {
        boolean valid = false;

        if (StringUtils.equals(appType, "crm")) {
            valid = verifyCRMCredentials(appName, appUsername, appPassword);
        } else {
            // TODO - add verification for forum and community
            log.warn("we don't know how to verify credentials for app type " + appType);
        }

        return valid;
    }

    private boolean verifyCRMCredentials(String appName, String appUsername, String appPassword) {
        try {
            XmlRpcClient client = new XmlRpcClient(buildAppUrl("crm", appName) + "/api/xmlrpc");
            Vector<String> params = new Vector<String>();

            log.debug("attempting to verify crm credentials at url " + client.getURL() + " with vendor key " + crmVendorKey);

            params.add(crmVendorKey);
            params.add(appUsername);
            params.add(DigestUtils.md5Hex(appPassword));

            String tempKey = (String) client.execute("DataService.getTemporaryKey", params);

            if (tempKey != null) {
                log.debug("web service produced a temp key: " + tempKey);

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
     * Calls out to the Community web service to try to create a new user.
     */
    public UserAccount registerCommunityUserAccount(User user, CommunityAccountDetails details) throws RestClientException, UsernameTakenException, CASMappingException {
        RestTemplate restTemplate = new RestTemplate();

        log.info("preparing REST call to " + forumBase);

        // TODO - re-enable this
        // TODO - shouldn't be a get, use POST here...
        //String result = restTemplate.getForObject("{base}/rest.php/user/addnewuser/{username}/{email}?key={apiKey}", String.class, forumBase, details.getDisplayName(), details.getNotificationEmailAddress(), forumApiKey);

        //log.debug("REST response: " + result);

        //JSONObject returnValue = (JSONObject) JSONValue.parse(result);
        //Boolean hasError = (Boolean) returnValue.get("error");

        //if (hasError) {
        //    throw new UsernameTakenException("the display name [" + forumDisplayName + "] is already taken");
        //} else {
//            return associateAccountToUser(user, "community", "Infusionsoft Community", String.valueOf(returnValue.get("username")));
        //}

        UserAccount account = infusionsoftDataService.associateAccountToUser(user, "community", "Infusionsoft Community", details.getDisplayName());

        details.setUserAccount(account);

        hibernateTemplate.save(details);

        return account;
    }

    /**
     * Calls out to the Community web service to try to update a user profile.
     */
    public void updateCommunityUserAccount(User user, CommunityAccountDetails details) throws RestClientException, UsernameTakenException {
        RestTemplate restTemplate = new RestTemplate();

        log.info("preparing REST call to " + forumBase);

        // TODO - re-enable this with the correct service call
        // TODO - shouldn't be a get, use POST here...
        //String result = restTemplate.getForObject("{base}/rest.php/user/addnewuser/{username}/{email}?key={apiKey}", String.class, forumBase, details.getDisplayName(), details.getNotificationEmailAddress(), forumApiKey);

        //log.debug("REST response: " + result);

        //JSONObject returnValue = (JSONObject) JSONValue.parse(result);
        //Boolean hasError = (Boolean) returnValue.get("error");

        //if (hasError) {
        //    throw new UsernameTakenException("the display name [" + forumDisplayName + "] is already taken");
        //} else {
//            return associateAccountToUser(user, "community", "Infusionsoft Community", String.valueOf(returnValue.get("username")));
        //}

        details.getUserAccount().setAppUsername(details.getDisplayName());
        hibernateTemplate.update(details.getUserAccount());
        hibernateTemplate.update(details);
    }


    /**
     * Creates (or updates) a CAS ticket granting ticket. Sometimes this needs to be called after an attributes change,
     * so they are refreshed properly. It should only be called when the user is already authenticated and trusted,
     * since it automatically creates a new session without validating the password.
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

        log.info("registered new user account " + username);
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
                log.info("found CASTGC cookie with value " + cookie.getValue());

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
            if (account.getAppType().equals("community")) {
                return true;
            }
        }

        return false;
    }

    public void setCentralAuthenticationService(CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    public void setServiceRegistryDao(ServiceRegistryDao serviceRegistryDao) {
        this.serviceRegistryDao = serviceRegistryDao;
    }

    public void setTicketRegistry(TicketRegistry ticketRegistry) {
        this.ticketRegistry = ticketRegistry;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setTicketIdGenerator(UniqueTicketIdGenerator ticketIdGenerator) {
        this.ticketIdGenerator = ticketIdGenerator;
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

    public String getCommunityDomain() {
        return communityDomain;
    }

    public void setCommunityDomain(String communityDomain) {
        this.communityDomain = communityDomain;
    }

    public InfusionsoftDataService getInfusionsoftDataService() {
        return infusionsoftDataService;
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
}
