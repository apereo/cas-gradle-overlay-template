package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.authentication.LetMeInCredentials;
import org.apache.log4j.Logger;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AutoLoginService {

    private static final Logger log = Logger.getLogger(AutoLoginService.class);

    @Autowired
    private CentralAuthenticationService centralAuthenticationService;

    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    @Autowired
    private TicketRegistry ticketRegistry;

    public boolean autoLogin(String username, HttpServletRequest request, HttpServletResponse response) {
        boolean retVal;
        try {
            killTGT(request);
            LetMeInCredentials letMeInCredentials = new LetMeInCredentials();
            letMeInCredentials.setUsername(username);
            String ticketGrantingTicketId = centralAuthenticationService.createTicketGrantingTicket(letMeInCredentials);

            this.ticketGrantingTicketCookieGenerator.addCookie(request, response, ticketGrantingTicketId);

            retVal = true;
        } catch (Exception e) {
            log.error(e);
            retVal = false;
        }
        return retVal;
    }

    public void killTGT(HttpServletRequest request) {
        //killing the TGT will force CAS to kill the security session for the service the TGT was established for
        String oldTicketGrantingTicketId = ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
        if (oldTicketGrantingTicketId != null) {
            TicketGrantingTicket ticket = (TicketGrantingTicket) ticketRegistry.getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);

            if (ticket != null) {
                centralAuthenticationService.destroyTicketGrantingTicket(oldTicketGrantingTicketId);
            }
        }
    }
}
