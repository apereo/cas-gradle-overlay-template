package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.domain.OAuthClient;
import org.apereo.cas.infusionsoft.domain.OAuthServiceConfig;
import org.apereo.cas.infusionsoft.oauth.dto.OAuthGrantType;
import org.apereo.cas.infusionsoft.oauth.exceptions.OAuthInvalidRequestException;
import org.apereo.cas.infusionsoft.services.OAuthClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A refresh token provider
 */
@Component
public class OAuthRefreshTokenProvider implements OAuthFilterTokenProvider {

    @Autowired
    private OAuthClientService oAuthClientService;

    /**
     * @param request            request
     * @param response           response
     * @param scope              scope
     * @param application        application
     * @param grantType          grantType
     * @param oAuthServiceConfig oAuthServiceConfig
     * @param clientId           clientId
     * @param clientSecret       clientSecret
     */
    @Override
    public OAuthRefreshAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {
        String refreshToken = StringUtils.defaultString(request.getParameter("refresh_token")).trim();

        if (!OAuthGrantType.REFRESH.isValueEqual(grantType)) {
            return null;
        }

        if (StringUtils.isBlank(clientId)) {
            throw new OAuthInvalidRequestException("oauth.exception.clientId.missing");
        }
        if (StringUtils.isBlank(refreshToken)) {
            throw new OAuthInvalidRequestException("oauth.exception.refreshToken.missing");
        }

        if (StringUtils.isBlank(clientSecret)) {
            OAuthClient oAuthClient = oAuthClientService.loadOAuthClient(clientId);
            if (oAuthClient != null) {
                clientSecret = oAuthClient.getClientSecret();
            }
        }
        if (StringUtils.isBlank(clientSecret)) {
            throw new OAuthInvalidRequestException("oauth.exception.clientSecret.missing");
        }

        String originHeader = request.getHeader("Origin");
        if (StringUtils.isNotBlank(originHeader)) {
            response.setHeader("Access-Control-Allow-Origin", originHeader);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "POST");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authentication");
            response.setHeader("Access-Control-Max-Age", "300");
        }

        return new OAuthRefreshAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, grantType, refreshToken);
    }
}
