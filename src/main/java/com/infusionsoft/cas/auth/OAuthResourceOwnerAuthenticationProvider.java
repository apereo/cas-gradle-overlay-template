package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.exceptions.OAuthUnauthorizedClientException;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class OAuthResourceOwnerAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    OAuthService oAuthService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthResourceOwnerAuthenticationToken retVal = null;
        OAuthResourceOwnerAuthenticationToken oAuthResourceOwnerAuthenticationToken = (OAuthResourceOwnerAuthenticationToken) authentication;

        if (oAuthResourceOwnerAuthenticationToken != null) {
            if (oAuthService.isClientAuthorizedForResourceOwnerGrantType(oAuthResourceOwnerAuthenticationToken.getClientId())) {
                LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(oAuthResourceOwnerAuthenticationToken.getPrincipal().toString(), oAuthResourceOwnerAuthenticationToken.getCredentials().toString());
                if (loginResult.getLoginStatus().isSuccessful()) {
                    retVal = new OAuthResourceOwnerAuthenticationToken(loginResult.getUser(), null, oAuthResourceOwnerAuthenticationToken.getServiceConfig(), oAuthResourceOwnerAuthenticationToken.getClientId(), oAuthResourceOwnerAuthenticationToken.getClientSecret(), oAuthResourceOwnerAuthenticationToken.getScope(), oAuthResourceOwnerAuthenticationToken.getGrantType(), oAuthResourceOwnerAuthenticationToken.getApplication(), loginResult.getUser().getAuthorities());
                }
            } else {
                throw new OAuthUnauthorizedClientException("oauth.exception.client.not.trusted.mobile");
            }
        }

        return retVal;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuthResourceOwnerAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
