package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.exceptions.OAuthUnauthorizedClientException;
import com.infusionsoft.cas.oauth.services.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class OAuthClientCredentialsAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    OAuthService oAuthService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthClientCredentialsAuthenticationToken retVal = null;
        OAuthClientCredentialsAuthenticationToken oAuthClientCredentialsAuthenticationToken = (OAuthClientCredentialsAuthenticationToken) authentication;

        if (oAuthClientCredentialsAuthenticationToken != null) {
            if (oAuthService.isClientAuthorizedForClientCredentialsGrantType(oAuthClientCredentialsAuthenticationToken.getClientId())) {
                retVal = new OAuthClientCredentialsAuthenticationToken(oAuthClientCredentialsAuthenticationToken.getServiceConfig(), oAuthClientCredentialsAuthenticationToken.getClientId(), oAuthClientCredentialsAuthenticationToken.getClientSecret(), oAuthClientCredentialsAuthenticationToken.getScope(), oAuthClientCredentialsAuthenticationToken.getGrantType(), oAuthClientCredentialsAuthenticationToken.getApplication());
            } else {
                throw new OAuthUnauthorizedClientException("oauth.exception.client.not.trusted.service");
            }
        }

        return retVal;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuthClientCredentialsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
