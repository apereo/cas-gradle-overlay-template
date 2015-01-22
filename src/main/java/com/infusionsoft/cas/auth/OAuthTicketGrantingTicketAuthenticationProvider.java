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

        // TODO: ACCESS_TOKEN_TODO: look these up somewhere based on clientId
        String clientSecret = "hard coded for POC";
        boolean anonymousAllowed = true;

        TicketGrantingTicket ticketGrantingTicket = token.getTicketGrantingTicket();
        if ((ticketGrantingTicket == null || ticketGrantingTicket.isExpired())) {
            if (!anonymousAllowed) {
                throw new AccessDeniedException("No valid ticket granting ticket found and anonymous access disabled");
            }
            // Create an anonymous authentication token
            return new OAuthTicketGrantingTicketAuthenticationToken(OAuthAuthenticationToken.ANONYMOUS_USER, null, clientId, clientSecret, token.getScope(), token.getGrantType(), token.getApplication(), ticketGrantingTicket, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        } else {
            // Create authentication token for a particular user
            Principal principal = ticketGrantingTicket.getAuthentication().getPrincipal();
            User user = userService.loadUser(principal.getId());
            return new OAuthTicketGrantingTicketAuthenticationToken(user, null, clientId, clientSecret, token.getScope(), token.getGrantType(), token.getApplication(), ticketGrantingTicket, user.getAuthorities());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(OAuthTicketGrantingTicketAuthenticationToken.class);
    }
}