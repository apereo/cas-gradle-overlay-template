package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component("oauthClientCredentialsAuthenticationProvider")
public class OAuthClientCredentialsAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthClientCredentialAuthenticationToken retVal = null;
        OAuthClientCredentialAuthenticationToken oAuthClientCredentialAuthenticationToken = (OAuthClientCredentialAuthenticationToken) authentication;

        if (oAuthClientCredentialAuthenticationToken != null) {
            LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(oAuthClientCredentialAuthenticationToken.getName(), oAuthClientCredentialAuthenticationToken.getCredentials().toString());
            if (loginResult.getLoginStatus().isSuccessful()) {
                retVal = new OAuthClientCredentialAuthenticationToken(loginResult.getUser(), null, oAuthClientCredentialAuthenticationToken.getClientId(), oAuthClientCredentialAuthenticationToken.getClientSecret(), loginResult.getUser().getAuthorities());
            }
        }

        return retVal;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(OAuthClientCredentialAuthenticationToken.class);
    }
}
