package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidGrantException;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
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
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private OAuthService oAuthService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthResourceOwnerAuthenticationToken retVal = null;
        OAuthResourceOwnerAuthenticationToken token = (OAuthResourceOwnerAuthenticationToken) authentication;

        if (token.getServiceConfig() == null) {
            throw new OAuthInvalidRequestException("oauth.exception.service.key.not.found");
        }

        if (oAuthService.isClientAuthorizedForResourceOwnerGrantType(token.getClientId())) {
            LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(token.getPrincipal().toString(), token.getCredentials().toString());
            if (loginResult.getLoginStatus().isSuccessful()) {
                retVal = new OAuthResourceOwnerAuthenticationToken(loginResult.getUser(), null, token.getServiceConfig(), token.getClientId(), token.getClientSecret(), token.getScope(), token.getGrantType(), token.getApplication(), loginResult.getUser().getAuthorities());
            } else {
                throw new OAuthInvalidGrantException();
            }
        } else {
            throw new OAuthUnauthorizedClientException("oauth.exception.client.not.trusted.mobile");
        }

        return retVal;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuthResourceOwnerAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
