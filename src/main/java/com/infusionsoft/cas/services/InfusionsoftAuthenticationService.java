package com.infusionsoft.cas.services;

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
import org.springframework.orm.hibernate3.HibernateTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
     * Builds a URL for redirecting users to an app.
     */
    public String buildAppUrl(String appType, String appName) {
        // TODO
        return "http://www.google.com";
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
    public UserAccount associateAccountToUser(User user, String appType, String appName, String appUsername) {
        UserAccount account = new UserAccount();

        account.setUser(user);
        account.setAppType(appType);
        account.setAppName(appName);
        account.setAppUsername(appUsername);

        user.getAccounts().add(account);

        hibernateTemplate.save(account);
        hibernateTemplate.update(user);

        return account;
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
}
