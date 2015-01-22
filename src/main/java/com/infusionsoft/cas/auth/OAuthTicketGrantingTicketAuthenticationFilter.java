package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class OAuthTicketGrantingTicketAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private OAuthService oAuthService;

    @Override
    protected OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, String scope, String application, String grantType, String clientId, String clientSecret) {
        TicketGrantingTicket ticketGrantingTicket = infusionsoftAuthenticationService.getTicketGrantingTicket(request);

        if (!oAuthService.isTicketGrantingTicketGrantType(grantType) || clientId == null) {
            return null;
        }

        return new OAuthTicketGrantingTicketAuthenticationToken(null, null, clientId, null, scope, grantType, application, ticketGrantingTicket);
    }

}
