package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.domain.OAuthServiceConfig;
import org.apereo.cas.infusionsoft.oauth.exceptions.OAuthUnsupportedGrantTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * A Spring Security Filter that is responsible for extracting client credentials and user credentials
 * to be authenticated via CAS and Mashery. This implementation uses all available filter token providers.
 */
@Component
public class OAuthAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    @Autowired
    private List<OAuthFilterTokenProvider> tokenProviders;

    @Override
    protected OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {
        for (OAuthFilterTokenProvider tokenProvider : tokenProviders) {
            final OAuthAuthenticationToken token = tokenProvider.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);
            if (token != null) {
                return token;
            }
        }
        throw new OAuthUnsupportedGrantTypeException();
    }

}
