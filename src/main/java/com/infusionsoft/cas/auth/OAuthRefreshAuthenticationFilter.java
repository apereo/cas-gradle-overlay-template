package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthClient;
import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidClientException;
import com.infusionsoft.cas.services.OAuthClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Spring Security Filter that is responsible for extracting client credentials and user credentials
 * to be authenticated via CAS and Mashery.
 * <p/>
 * The filter was original copied from BasicAuthenticationFilter and modified from there.
 */
@Component
public class OAuthRefreshAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    @Autowired
    OAuthClientService oAuthClientService;

    @Override
    protected OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {
        String refreshToken = StringUtils.defaultString(request.getParameter("refresh_token")).trim();

        if (!OAuthGrantType.REFRESH.isValueEqual(grantType) || clientId == null) {
            return null;
        }

        if(StringUtils.isEmpty(clientSecret)) {
            OAuthClient oAuthClient = oAuthClientService.loadOAuthClient(clientId);
            if (oAuthClient == null) {
                throw new OAuthInvalidClientException();
            } else {
                clientSecret = oAuthClient.getClientSecret();
            }
        }


        return new OAuthRefreshAuthenticationToken(null, null, oAuthServiceConfig, clientId, clientSecret, grantType, refreshToken);
    }
}
