package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Spring Security Filter that is responsible for extracting client credentials
 * to be authenticated via Mashery.
 *
 * The filter was original copied from BasicAuthenticationFilter and modified from there.
 */
@Component
public class OAuthClientCredentialsAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    @Override
    protected OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {

        if (!OAuthGrantType.CLIENT_CREDENTIALS.isValueEqual(grantType) || clientId == null || clientSecret == null) {
            return null;
        }

        return new OAuthClientCredentialsAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application);
    }
}
