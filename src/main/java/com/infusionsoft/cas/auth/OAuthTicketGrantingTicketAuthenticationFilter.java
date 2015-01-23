package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthClient;
import com.infusionsoft.cas.exceptions.BadRequestException;
import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.OAuthClientService;
import com.infusionsoft.cas.web.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class OAuthTicketGrantingTicketAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private OAuthClientService oAuthClientService;

    private static String USER_TRACKING_COOKIE_NAME = "userUUID";

    @Override
    protected OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, String clientId, String clientSecret) {
        TicketGrantingTicket ticketGrantingTicket = infusionsoftAuthenticationService.getTicketGrantingTicket(request);

        if (!OAuthGrantType.EXTENDED_TICKET_GRANTING_TICKET.isValueEqual(grantType) || clientId == null) {
            return null;
        }

        OAuthClient oAuthClient = oAuthClientService.loadOAuthClient(clientId);
        if(oAuthClient == null) {
            throw new BadCredentialsException("Invalid Client Id");
        }

        String originHeader = request.getHeader("ORIGIN");
        if(StringUtils.isBlank(originHeader)) {
            throw new BadRequestException("No Origin Headers Attached");
        }

        if(!oAuthClientService.doesServiceMatchHeader(oAuthClient, originHeader)){
            throw new AccessDeniedException("Origin not allowed for Client Id");
        }

        String userTrackingCookieValue = CookieUtil.extractCookieValue(request, USER_TRACKING_COOKIE_NAME);
        if (StringUtils.isBlank(userTrackingCookieValue)) {
            userTrackingCookieValue = UUID.randomUUID().toString();
            CookieUtil.setCookie(response, USER_TRACKING_COOKIE_NAME, userTrackingCookieValue, Integer.MAX_VALUE);
        }

        response.setHeader("Access-Control-Allow-Origin", originHeader);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Request-Method", "POST");
        response.setHeader("Access-Control-Max-Age", "0");

        return new OAuthTicketGrantingTicketAuthenticationToken(null, null, clientId, oAuthClient.getClientSecret(), scope, grantType, application, userTrackingCookieValue, ticketGrantingTicket);
    }

}
