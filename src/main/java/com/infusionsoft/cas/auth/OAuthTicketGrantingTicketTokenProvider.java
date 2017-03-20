package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthClient;
import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import com.infusionsoft.cas.oauth.exceptions.OAuthAccessDeniedException;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidClientException;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.OAuthClientService;
import com.infusionsoft.cas.web.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * A ticket granting ticket grant token provider
 */
@Component
public class OAuthTicketGrantingTicketTokenProvider implements OAuthFilterTokenProvider {

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private OAuthClientService oAuthClientService;

    private static final String USER_TRACKING_COOKIE_NAME = "userUUID";

    @Override
    public OAuthTicketGrantingTicketAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {
        TicketGrantingTicket ticketGrantingTicket = infusionsoftAuthenticationService.getTicketGrantingTicket(request);

        if (!OAuthGrantType.EXTENDED_TICKET_GRANTING_TICKET.isValueEqual(grantType)) {
            return null;
        }

        if (StringUtils.isBlank(clientId)) {
            throw new OAuthInvalidRequestException("oauth.exception.clientId.missing");
        }

        OAuthClient oAuthClient = oAuthClientService.loadOAuthClient(clientId);
        if (oAuthClient == null) {
            throw new OAuthInvalidClientException();
        }

        String originHeader = request.getHeader("Origin");
        if (StringUtils.isBlank(originHeader)) {
            throw new OAuthInvalidRequestException("oauth.exception.origin.missing");
        }

        if (!oAuthClientService.isOriginAllowedByOAuthClient(oAuthClient, originHeader)) {
            throw new OAuthAccessDeniedException("oauth.exception.origin.not.authorized");
        }

        String userTrackingCookieValue = CookieUtil.extractCookieValue(request, USER_TRACKING_COOKIE_NAME);
        if (StringUtils.isBlank(userTrackingCookieValue)) {
            userTrackingCookieValue = UUID.randomUUID().toString();
            CookieUtil.setCookie(response, USER_TRACKING_COOKIE_NAME, userTrackingCookieValue, Integer.MAX_VALUE);
        }

        response.setHeader("Access-Control-Allow-Origin", originHeader);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authentication");
        // Disable caching, since whether the request is allowed or not depends on the client_id passed in, not just the URL
        response.setHeader("Access-Control-Max-Age", "0");

        return new OAuthTicketGrantingTicketAuthenticationToken(null, null, oAuthServiceConfig, clientId, oAuthClient.getClientSecret(), scope, grantType, application, userTrackingCookieValue, ticketGrantingTicket);
    }

}
