package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.exceptions.OAuthUnsupportedGrantTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Spring Security Filter that is responsible for extracting client credentials and user credentials
 * to be authenticated via CAS and Mashery. The same as {@link OAuthAuthenticationFilter} except that it
 * only supports refresh and resource owner grants, for the legacy token endpoint.
 */
@Component
public class OAuthLegacyAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    @Autowired
    private OAuthRefreshTokenProvider oAuthRefreshTokenProvider;

    @Autowired
    private OAuthResourceOwnerTokenProvider oAuthResourceOwnerTokenProvider;

    @Override
    protected OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {
        OAuthAuthenticationToken token = oAuthRefreshTokenProvider.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);
        if (token == null) {
            token = oAuthResourceOwnerTokenProvider.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);
        }
        if (token == null) {
            throw new OAuthUnsupportedGrantTypeException();
        }
        return token;
    }

}
