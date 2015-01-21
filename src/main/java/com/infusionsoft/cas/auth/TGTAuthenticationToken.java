package com.infusionsoft.cas.auth;

import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * This is an authentication token which contains a valid ticket granting ticket
 */
public class TGTAuthenticationToken extends AbstractAuthenticationToken {

    private TicketGrantingTicket ticketGrantingTicket;

    public TGTAuthenticationToken(TicketGrantingTicket ticketGrantingTicket) {
        super(null);
        this.ticketGrantingTicket = ticketGrantingTicket;
        setAuthenticated(false);
    }

    public Object getCredentials() {
        return null;
    }

    public Object getPrincipal() {
        return null;
    }

    public TicketGrantingTicket getTicketGrantingTicket() {
        return ticketGrantingTicket;
    }
}