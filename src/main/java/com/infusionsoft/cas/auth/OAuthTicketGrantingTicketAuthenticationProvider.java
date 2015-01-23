package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.oauth.exceptions.OAuthException;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.UserService;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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

    @Autowired
    private OAuthService oAuthService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthTicketGrantingTicketAuthenticationToken token = (OAuthTicketGrantingTicketAuthenticationToken) authentication;
        String clientId = token.getClientId();

        // Validate the clientId is authorized for extended grants
        try {
            if (!oAuthService.isClientAuthorizedForExtendedGrantType(clientId)) {
                throw new AccessDeniedException("Client is not authorized for extended grant type");
            }
        } catch (OAuthException e) {
            throw new AccessDeniedException("Unable to determine if client is authorized for extended grant type", e);
        }

        TicketGrantingTicket ticketGrantingTicket = token.getTicketGrantingTicket();
        if ((ticketGrantingTicket == null || ticketGrantingTicket.isExpired())) {

            // Create an anonymous authentication token
            return new OAuthTicketGrantingTicketAuthenticationToken("anonymous-" + token.getTrackingUUID(), null, clientId, token.getClientSecret(), token.getScope(), token.getGrantType(), token.getApplication(), token.getTrackingUUID(), ticketGrantingTicket, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        } else {
            // Create authentication token for a particular user
            Principal principal = ticketGrantingTicket.getAuthentication().getPrincipal();
            User user = userService.loadUser(principal.getId());
            return new OAuthTicketGrantingTicketAuthenticationToken(user, null, clientId, token.getClientSecret(), token.getScope(), token.getGrantType(), token.getApplication(), token.getTrackingUUID(), ticketGrantingTicket, user.getAuthorities());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(OAuthTicketGrantingTicketAuthenticationToken.class);
    }
}