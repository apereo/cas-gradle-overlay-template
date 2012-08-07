package com.infusionsoft.cas.services;

import com.infusionsoft.cas.exceptions.CASMappingException;
import com.infusionsoft.cas.exceptions.UsernameTakenException;
import com.infusionsoft.cas.types.CommunityAccountDetails;
import com.infusionsoft.cas.types.PendingUserAccount;
import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.services.ServiceRegistryDao;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility that handles Spring Security and CAS native authentication tricks.
 */
public class InfusionsoftAuthenticationService {
    private static final Logger log = Logger.getLogger(InfusionsoftAuthenticationService.class);

    private CentralAuthenticationService centralAuthenticationService;
    private ServiceRegistryDao serviceRegistryDao;
    private TicketRegistry ticketRegistry;
    private HibernateTemplate hibernateTemplate;
    private PasswordEncoder passwordEncoder;
    private UniqueTicketIdGenerator ticketIdGenerator;
    private String serverPrefix;
    private String crmProtocol;
    private String crmDomain;
    private String crmPort;
    private String customerHubDomain;
    private String communityDomain;
    private String forumBase;
    private String forumApiKey;

    /**
     * Guesses an app name from a URL, or null if there isn't one to be found.
     */
    public String guessAppName(URL url) {
        String host = url.getHost().toLowerCase();

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
     * Creates a unique, random password recovery code for a user.
     */
    public synchronized String createPasswordRecoveryCode(User user) {
        String recoveryCode = RandomStringUtils.random(12, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        while (findUserByRecoveryCode(recoveryCode) != null) {
            recoveryCode = RandomStringUtils.random(12, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }

        user.setPasswordRecoveryCode(recoveryCode);

        hibernateTemplate.update(user);

        return user.getPasswordRecoveryCode();
    }

    /**
     * Attempts to find a user by their recovery code.
     */
    public User findUserByRecoveryCode(String recoveryCode) {
        List<User> users = (List<User>) hibernateTemplate.find("from User where passwordRecoveryCode = ?", recoveryCode);

        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }

    /**
     * Fetches details for a community account, if available.
     */
    public CommunityAccountDetails findCommunityAccountDetails(UserAccount account) {
        List<CommunityAccountDetails> details = (List<CommunityAccountDetails>) hibernateTemplate.find("from CommunityAccountDetails where userAccount = ?", account);

        if (details.size() > 0) {
            return details.get(0);
        } else {
            return null;
        }
    }

    /**
     * Finds a user account by id, but only if it belongs to a given user.
     */
    public UserAccount findUserAccount(User user, Long accountId) {
        List<UserAccount> accounts = (List<UserAccount>) hibernateTemplate.find("from UserAccount where user = ? and id = ?", user, accountId);

        if (accounts.size() > 0) {
            return accounts.get(0);
        } else {
            return null;
        }
    }

    /**
     * Returns a user's accounts, sorted by type and name for consistency.
     */
    public List<UserAccount> getSortedUserAccounts(User user) {
        List<UserAccount> accounts = new ArrayList<UserAccount>();

        // TODO - combine these, someday when we're feeling really virtuous
        accounts.addAll(hibernateTemplate.find("from UserAccount where user = ? and appType = ? order by appName", user, "crm"));
        accounts.addAll(hibernateTemplate.find("from UserAccount where user = ? and appType = ? order by appName", user, "community"));
        accounts.addAll(hibernateTemplate.find("from UserAccount where user = ? and appType = ? order by appName", user, "customerhub"));

        return accounts;
    }

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
     * Checks the legacy credentials of an app.
     */
    public boolean verifyAppCredentials(String appType, String appName, String appUsername, String appPassword) {
        // TODO
        return true;
    }

    /**
     * Associates an external account to a CAS user.
     */
    public UserAccount associateAccountToUser(User user, String appType, String appName, String appUsername) throws CASMappingException {
        UserAccount account = new UserAccount();

        account.setUser(user);
        account.setAppType(appType);
        account.setAppName(appName);
        account.setAppUsername(appUsername);

        user.getAccounts().add(account);

        try {
            hibernateTemplate.save(account);
            hibernateTemplate.update(user);
        } catch (Exception e) {
            throw new CASMappingException("failed to associate user to app account", e);
        }


        return account;
    }

    /**
     * Tries to associate a user with a pending registration. If successful, this
     * will return the newly associated user account.
     */
    public UserAccount associateNewUser(User user, String registrationCode) throws CASMappingException {
        PendingUserAccount pendingAccount = findPendingUserAccount(registrationCode);
        UserAccount account = new UserAccount();

        account.setUser(user);
        account.setAppName(pendingAccount.getAppName());
        account.setAppType(pendingAccount.getAppType());
        account.setAppUsername(pendingAccount.getAppUsername());

        user.getAccounts().add(account);

        try {
            hibernateTemplate.save(account);
            hibernateTemplate.update(user);
            hibernateTemplate.delete(pendingAccount);

            log.info("associated new user to " + account.getAppName() + "/" + account.getAppType());
        } catch (Exception e) {
            throw new CASMappingException("failed to associate new user to registration code " + registrationCode, e);
        }

        return account;
    }

    /**
     * Finds a pending user account by its unique registration code.
     */
    public PendingUserAccount findPendingUserAccount(String registrationCode) {
        List<PendingUserAccount> accounts = hibernateTemplate.find("from PendingUserAccount where registrationCode = ?", registrationCode);

        if (accounts.size() > 0) {
            return accounts.get(0);
        } else {
            return null;
        }
    }

    /**
     * Checks if a user's existing password is valid. We need this for when an already logged in user wants
     * to update his user profile.
     */
    public boolean isPasswordValid(User user, String password) {
        String passwordEncoded = passwordEncoder.encode(password);
        List<User> users = (List<User>) hibernateTemplate.find("from User where username = ? and password = ?", user.getUsername(), passwordEncoded);

        return users.size() > 0;
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

        UserAccount account = associateAccountToUser(user, "community", "Infusionsoft Community", details.getDisplayName());

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
     * so they are refreshed properly.
     */
    public void createTicketGrantingTicket(String username, String password, HttpServletRequest request, HttpServletResponse response) throws TicketException {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials();

        credentials.setUsername(username);
        credentials.setPassword(password);

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
}
