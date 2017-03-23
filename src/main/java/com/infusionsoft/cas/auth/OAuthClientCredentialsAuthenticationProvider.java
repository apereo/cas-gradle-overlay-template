package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.dto.OAuthApplication;
import com.infusionsoft.cas.oauth.exceptions.OAuthUnauthorizedClientException;
import com.infusionsoft.cas.oauth.services.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OAuthClientCredentialsAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private OAuthService oAuthService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthClientCredentialsAuthenticationToken token = (OAuthClientCredentialsAuthenticationToken) authentication;

        final String clientId = token.getClientId();
        if (oAuthService.isClientAuthorizedForClientCredentialsGrantType(clientId)) {
            final OAuthServiceConfig serviceConfig = token.getServiceConfig();
            final OAuthApplication application = oAuthService.fetchApplication(serviceConfig.getServiceKey(), clientId, null, "code");
            final UUID applicationUuid = application.getUuid();

            return new OAuthClientCredentialsAuthenticationToken(applicationUuid.toString(), serviceConfig, clientId, token.getClientSecret(), token.getScope(), token.getGrantType(), token.getApplication(), null);
        } else {
            throw new OAuthUnauthorizedClientException("oauth.exception.client.not.trusted.service");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuthClientCredentialsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
