package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.web.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class OAuthTicketGrantingTicketAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    private static String USER_TRACKING_COOKIE_NAME = "userUUID";

    @Override
    protected OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, String clientId, String clientSecret) {
        TicketGrantingTicket ticketGrantingTicket = infusionsoftAuthenticationService.getTicketGrantingTicket(request);

        if (!OAuthGrantType.EXTENDED_TICKET_GRANTING_TICKET.isValueEqual(grantType) || clientId == null) {
            return null;
        }

        String userTrackingCookieValue = CookieUtil.extractCookieValue(request, USER_TRACKING_COOKIE_NAME);
        if (StringUtils.isBlank(userTrackingCookieValue)) {
            userTrackingCookieValue = UUID.randomUUID().toString();
            CookieUtil.setCookie(response, USER_TRACKING_COOKIE_NAME, userTrackingCookieValue, Integer.MAX_VALUE);
        }
        return new OAuthTicketGrantingTicketAuthenticationToken(null, null, clientId, null, scope, grantType, application, userTrackingCookieValue, ticketGrantingTicket);
    }

}
