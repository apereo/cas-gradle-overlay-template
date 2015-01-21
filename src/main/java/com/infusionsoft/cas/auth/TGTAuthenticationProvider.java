package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.services.UserService;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class TGTAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TGTAuthenticationToken tgtAuthenticationToken = (TGTAuthenticationToken) authentication;
        TicketGrantingTicket ticketGrantingTicket = tgtAuthenticationToken.getTicketGrantingTicket();

        if (ticketGrantingTicket == null || ticketGrantingTicket.isExpired()) {
            throw new AccessDeniedException("No valid ticket granting ticket found");
        }

        Principal principal = ticketGrantingTicket.getAuthentication().getPrincipal();
        User user = userService.loadUser(principal.getId());
        return new UsernamePasswordAuthenticationToken(user, ticketGrantingTicket, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(TGTAuthenticationToken.class);
    }
}