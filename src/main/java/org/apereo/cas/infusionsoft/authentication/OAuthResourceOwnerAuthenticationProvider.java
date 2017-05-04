package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.oauth.exceptions.OAuthInvalidGrantException;
import org.apereo.cas.infusionsoft.oauth.exceptions.OAuthUnauthorizedClientException;
import org.apereo.cas.infusionsoft.oauth.services.OAuthService;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
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

        if (oAuthService.isClientAuthorizedForResourceOwnerGrantType(token.getClientId())) {
            LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(token.getPrincipal().toString(), token.getCredentials().toString());
            if (loginResult.getLoginStatus().isSuccessful()) {
                retVal = new OAuthResourceOwnerAuthenticationToken(loginResult.getUser(), token.getServiceConfig(), token.getClientId(), token.getClientSecret(), token.getScope(), token.getGrantType(), token.getApplication(), loginResult.getUser().getAuthorities());
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
