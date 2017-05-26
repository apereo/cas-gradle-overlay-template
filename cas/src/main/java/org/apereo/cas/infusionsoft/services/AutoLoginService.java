package org.apereo.cas.infusionsoft.services;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationResult;
import org.apereo.cas.authentication.AuthenticationResultBuilder;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.DefaultAuthenticationResultBuilder;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.WebApplicationServiceFactory;
import org.apereo.cas.infusionsoft.authentication.LetMeInCredentials;
import org.apereo.cas.ticket.AbstractTicketException;
import org.apereo.cas.ticket.ServiceTicket;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.validation.Assertion;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AutoLoginService {

    private static final Logger log = LoggerFactory.getLogger(AutoLoginService.class);

    private CentralAuthenticationService centralAuthenticationService;
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;
    private TicketRegistry ticketRegistry;
    private AuthenticationSystemSupport authenticationSystemSupport;
    private WebApplicationServiceFactory webApplicationServiceFactory;

    public AutoLoginService(final CentralAuthenticationService centralAuthenticationService, final CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator, final TicketRegistry ticketRegistry, final AuthenticationSystemSupport authenticationSystemSupport, final WebApplicationServiceFactory webApplicationServiceFactory) {
        this.centralAuthenticationService = centralAuthenticationService;
        this.ticketGrantingTicketCookieGenerator = ticketGrantingTicketCookieGenerator;
        this.ticketRegistry = ticketRegistry;
        this.authenticationSystemSupport = authenticationSystemSupport;
        this.webApplicationServiceFactory = webApplicationServiceFactory;
    }

    /**
     * Login a user to a specific service based on a username.
     * Based on {@link org.apereo.cas.support.rest.TicketsResource#createTicketGrantingTicket(MultiValueMap, HttpServletRequest)} and
     * {@link org.apereo.cas.support.rest.TicketsResource#createServiceTicket(MultiValueMap, String)}
     *
     * @param serviceUrl
     * @param username
     * @param request
     * @param response
     * @return
     */
    public ServiceTicket autoLogin(String serviceUrl, String username, HttpServletRequest request, HttpServletResponse response) {
        ServiceTicket serviceTicket;
        try {
            killTGT(request);

            final LetMeInCredentials letMeInCredentials = new LetMeInCredentials(username);
            final Service service = webApplicationServiceFactory.createService(serviceUrl);
            final AuthenticationResult authenticationResult = authenticationSystemSupport.handleAndFinalizeSingleAuthenticationTransaction(null, letMeInCredentials);
            final TicketGrantingTicket ticketGrantingTicket = centralAuthenticationService.createTicketGrantingTicket(authenticationResult);
            final String ticketGrantingTicketId = ticketGrantingTicket.getId();
            serviceTicket = centralAuthenticationService.grantServiceTicket(ticketGrantingTicketId, service, authenticationResult);

            ticketGrantingTicketCookieGenerator.addCookie(request, response, ticketGrantingTicketId);
        } catch (Exception e) {
            log.error("Error during auto-login", e);
            serviceTicket = null;
        }
        return serviceTicket;
    }

    /**
     * Login a user to a specific service based on an existing TicketGrantingTicket.
     * Based on {@link org.apereo.cas.support.rest.TicketsResource#createServiceTicket(MultiValueMap, String)}
     *
     * @return
     */
    public ServiceTicket autoLogin(String serviceUrl, TicketGrantingTicket ticketGrantingTicket, HttpServletRequest request, HttpServletResponse response) {
        ServiceTicket serviceTicket;
        try {
            final AuthenticationResultBuilder builder = new DefaultAuthenticationResultBuilder(authenticationSystemSupport.getPrincipalElectionStrategy());
            final Service service = webApplicationServiceFactory.createService(serviceUrl);
            final AuthenticationResult authenticationResult = builder.collect(ticketGrantingTicket.getAuthentication()).build(service);
            final String ticketGrantingTicketId = ticketGrantingTicket.getId();
            serviceTicket = centralAuthenticationService.grantServiceTicket(ticketGrantingTicketId, service, authenticationResult);

            ticketGrantingTicketCookieGenerator.addCookie(request, response, ticketGrantingTicketId);
        } catch (Exception e) {
            log.error("Error during auto-login", e);
            serviceTicket = null;
        }
        return serviceTicket;
    }

    public TicketGrantingTicket getValidTGTFromRequest(HttpServletRequest request) {
        TicketGrantingTicket ticket = getTGTFromRequest(request);
        if (ticket != null && ticket.isExpired()) {
            ticket = null;
        }
        return ticket;
    }

    private TicketGrantingTicket getTGTFromRequest(HttpServletRequest request) {
        String oldTicketGrantingTicketId = ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
        TicketGrantingTicket ticket = null;
        if (oldTicketGrantingTicketId != null) {
            ticket = ticketRegistry.getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);
            if (ticket != null && ticket.isExpired()) {
                ticket = null;
            }
        }
        return ticket;
    }

    public void killTGT(HttpServletRequest request) {
        //killing the TGT will force CAS to kill the security session for the service the TGT was established for
        TicketGrantingTicket ticket = getTGTFromRequest(request);
        if (ticket != null) {
            centralAuthenticationService.destroyTicketGrantingTicket(ticket.getId());
        }
    }

    public TicketGrantingTicket validateServiceTicket(String serviceTicketId, String serviceUrl) {
        try {
            if (StringUtils.isNotBlank(serviceTicketId)) {
                final Service service = webApplicationServiceFactory.createService(serviceUrl);
                final Ticket serviceTicket = ticketRegistry.getTicket(serviceTicketId);
                final Assertion assertion = centralAuthenticationService.validateServiceTicket(serviceTicketId, service);
                if (assertion != null && serviceTicket != null) {
                    return serviceTicket.getGrantingTicket();
                }
            }
        } catch (AbstractTicketException ignored) {
        }
        return null;
    }
}
