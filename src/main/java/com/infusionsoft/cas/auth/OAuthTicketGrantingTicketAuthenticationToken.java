package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * This is an OAuth authentication token which contains a ticket granting ticket
 */
public class OAuthTicketGrantingTicketAuthenticationToken extends OAuthAuthenticationToken {

    private TicketGrantingTicket ticketGrantingTicket;

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>OAuthTicketGrantingTicketAuthenticationToken</code>, as the {@link
     * #isAuthenticated()} will return <code>false</code>.
     *
     * @param principal            principal
     * @param credentials          credentials
     * @param oAuthServiceConfig   oAuthServiceConfig
     * @param clientId             clientId
     * @param clientSecret         clientSecret
     * @param scope                scope
     * @param grantType            grantType
     * @param application          application
     * @param trackingUUID         trackingUUID
     * @param ticketGrantingTicket ticketGrantingTicket
     */
    public OAuthTicketGrantingTicketAuthenticationToken(Object principal, Object credentials, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String scope, String grantType, String application, String trackingUUID, TicketGrantingTicket ticketGrantingTicket) {
        super(principal, credentials, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, trackingUUID);
        this.ticketGrantingTicket = ticketGrantingTicket;
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or <code>AuthenticationProvider</code>
     * implementations that are satisfied with producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param principal            principal
     * @param credentials          credentials
     * @param oAuthServiceConfig   oAuthServiceConfig
     * @param clientId             clientId
     * @param clientSecret         clientSecret
     * @param scope                scope
     * @param grantType            grantType
     * @param application          application
     * @param trackingUUID         trackingUUID
     * @param ticketGrantingTicket ticketGrantingTicket
     * @param authorities          authorities
     */
    public OAuthTicketGrantingTicketAuthenticationToken(Object principal, Object credentials, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String scope, String grantType, String application, String trackingUUID, TicketGrantingTicket ticketGrantingTicket, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, trackingUUID, authorities);
        this.ticketGrantingTicket = ticketGrantingTicket;
    }

    public TicketGrantingTicket getTicketGrantingTicket() {
        return ticketGrantingTicket;
    }

}