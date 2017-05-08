package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationResult;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.infusionsoft.authentication.LetMeInCredentials;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AutoLoginService {

    private static final Logger log = LoggerFactory.getLogger(AutoLoginService.class);

    private CentralAuthenticationService centralAuthenticationService;
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;
    private TicketRegistry ticketRegistry;
    private AuthenticationSystemSupport authenticationSystemSupport;

    public AutoLoginService(final CentralAuthenticationService centralAuthenticationService, final CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator, final TicketRegistry ticketRegistry, final AuthenticationSystemSupport authenticationSystemSupport) {
        this.centralAuthenticationService = centralAuthenticationService;
        this.ticketGrantingTicketCookieGenerator = ticketGrantingTicketCookieGenerator;
        this.ticketRegistry = ticketRegistry;
        this.authenticationSystemSupport = authenticationSystemSupport;
    }

    public boolean autoLogin(String username, HttpServletRequest request, HttpServletResponse response) {
        boolean retVal;
        try {
            killTGT(request);

            final LetMeInCredentials letMeInCredentials = new LetMeInCredentials(username);
            final AuthenticationResult authenticationResult = authenticationSystemSupport.handleAndFinalizeSingleAuthenticationTransaction(null, letMeInCredentials);
            final TicketGrantingTicket ticketGrantingTicket = centralAuthenticationService.createTicketGrantingTicket(authenticationResult);
            final String ticketGrantingTicketId = ticketGrantingTicket.getId();

            this.ticketGrantingTicketCookieGenerator.addCookie(request, response, ticketGrantingTicketId);

            retVal = true;
        } catch (Exception e) {
            log.error("Error during auto-login", e);
            retVal = false;
        }
        return retVal;
    }

    public void killTGT(HttpServletRequest request) {
        //killing the TGT will force CAS to kill the security session for the service the TGT was established for
        String oldTicketGrantingTicketId = ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
        if (oldTicketGrantingTicketId != null) {
            TicketGrantingTicket ticket = ticketRegistry.getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);

            if (ticket != null) {
                centralAuthenticationService.destroyTicketGrantingTicket(oldTicketGrantingTicketId);
            }
        }
    }

}
