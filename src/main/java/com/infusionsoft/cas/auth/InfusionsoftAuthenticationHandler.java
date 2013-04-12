package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.BadUsernameOrPasswordAuthenticationException;
import org.jasig.cas.authentication.handler.BlockedCredentialsAuthenticationException;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Infusionsoft implementation of the authentication handler.
 */
@Component
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    PasswordEncoder passwordEncoder;

    protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials credentials) throws AuthenticationException {
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(credentials.getUsername(), credentials.getPassword());

        switch (loginResult.getLoginStatus()) {
            case AccountLocked:
            case DisabledUser:
                throw new BlockedCredentialsAuthenticationException();
            case BadPassword:
            case NoSuchUser:
                throw new BadUsernameOrPasswordAuthenticationException();
            case PasswordExpired:
            case Success:
                return true;
            default:
                throw new IllegalStateException("Unknown value for loginResult: " + loginResult);
        }

    }
}
