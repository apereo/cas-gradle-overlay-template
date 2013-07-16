package com.infusionsoft.cas.services;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * User: joe.koberstein
 * Date: 7/16/13 * Time: 10:57 AM
 */
@Service
public class CasAuthenticationHelper {

    @Autowired
    TicketRegistry ticketRegistry;

    @Autowired
    CentralAuthenticationService centralAuthenticationService;

    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    public void killTicketGrantingCookieAndSecurityContext(HttpServletRequest request) {
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
