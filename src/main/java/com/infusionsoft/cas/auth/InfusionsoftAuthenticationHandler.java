package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.PasswordService;
import com.infusionsoft.cas.services.UserService;
import org.jasig.cas.authentication.handler.*;
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
    private PasswordService passwordService;

    @Autowired
    PasswordEncoder passwordEncoder;

    protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials credentials) throws AuthenticationException {
        boolean retVal;

        boolean passwordValid = passwordService.isPasswordValid(credentials.getUsername(), credentials.getPassword());

        if (passwordValid) {
            if(infusionsoftAuthenticationService.isAccountLocked(credentials.getUsername())) {
                infusionsoftAuthenticationService.recordLoginAttempt(credentials.getUsername(), false);
                throw new BlockedCredentialsAuthenticationException();
            } else {
                infusionsoftAuthenticationService.recordLoginAttempt(credentials.getUsername(), true);
                retVal = true;
                log.info("authenticated CAS user " + credentials.getUsername());
            }
        } else {
            infusionsoftAuthenticationService.recordLoginAttempt(credentials.getUsername(), false);
            throw new BadUsernameOrPasswordAuthenticationException();
        }

        return retVal;
    }
}
