package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;

        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(usernamePasswordAuthenticationToken.getName(), usernamePasswordAuthenticationToken.getCredentials().toString());
        if (loginResult.getLoginStatus().isSuccessful()) {
            return new UsernamePasswordAuthenticationToken(loginResult.getUser(), null, loginResult.getUser().getAuthorities());
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
