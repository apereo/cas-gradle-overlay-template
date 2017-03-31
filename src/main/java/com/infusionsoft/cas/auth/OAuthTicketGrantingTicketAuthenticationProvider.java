package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.oauth.exceptions.OAuthAccessDeniedException;
import com.infusionsoft.cas.services.UserService;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class OAuthTicketGrantingTicketAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthTicketGrantingTicketAuthenticationToken token = (OAuthTicketGrantingTicketAuthenticationToken) authentication;
        String clientId = token.getClientId();

        TicketGrantingTicket ticketGrantingTicket = token.getTicketGrantingTicket();
        if ((ticketGrantingTicket == null || ticketGrantingTicket.isExpired())) {

            if (!token.getServiceConfig().getAllowAnonymous()) {
                throw new OAuthAccessDeniedException("oauth.exception.anonymous.not.allowed");
            }

            // Create an anonymous authentication token
            final OAuthTicketGrantingTicketAuthenticationToken returnToken = new OAuthTicketGrantingTicketAuthenticationToken("anonymous-" + token.getTrackingUUID(), token.getServiceConfig(), clientId, token.getClientSecret(), token.getScope(), token.getGrantType(), token.getApplication(), token.getTrackingUUID(), ticketGrantingTicket, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
            returnToken.setAuthenticated(false);
            return returnToken;
        } else {
            // Create authentication token for a particular user
            Principal principal = ticketGrantingTicket.getAuthentication().getPrincipal();
            String username = principal.getId();
            User user = userService.loadUser(username);
            return new OAuthTicketGrantingTicketAuthenticationToken(user, token.getServiceConfig(), clientId, token.getClientSecret(), token.getScope(), token.getGrantType(), token.getApplication(), token.getTrackingUUID(), ticketGrantingTicket, user.getAuthorities());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuthTicketGrantingTicketAuthenticationToken.class.isAssignableFrom(authentication);
    }
}