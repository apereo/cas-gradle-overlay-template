package com.infusionsoft.cas.services;

import com.infusionsoft.cas.auth.LetMeInCredentials;
import org.apache.log4j.Logger;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.ticket.ExpirationPolicy;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.util.UniqueTicketIdGenerator;
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
    CentralAuthenticationService centralAuthenticationService;

    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    @Autowired
    TicketRegistry ticketRegistry;

    @Autowired
    @Qualifier("ticketGrantingTicketUniqueIdGenerator")
    UniqueTicketIdGenerator ticketGrantingTicketUniqueIdGenerator;

    @Autowired
    @Qualifier("grantingTicketExpirationPolicy")
    ExpirationPolicy ticketGrantingTicketExpirationPolicy;

    public boolean autoLogin(String username, HttpServletRequest request, HttpServletResponse response) {
        boolean retVal;

        try {
            String oldTicketGrantingTicketId = ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);

            if (oldTicketGrantingTicketId != null) {
                TicketGrantingTicket ticket = (TicketGrantingTicket) ticketRegistry.getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);

                if (ticket != null) {
                    centralAuthenticationService.destroyTicketGrantingTicket(oldTicketGrantingTicketId);
                }
            }

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
}
