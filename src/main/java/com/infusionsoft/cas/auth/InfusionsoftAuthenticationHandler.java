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
        if (credentials instanceof LetMeInCredentials) {
            return true;
        } else {
            LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(credentials.getUsername(), credentials.getPassword());

            switch (loginResult.getLoginStatus()) {
                case AccountLocked:
                case DisabledUser: // TODO: why is a disabled user getting the locked message?
                    throw new BlockedCredentialsAuthenticationException("login.lockedTooManyFailures");
                case BadPassword:
                case NoSuchUser:
                    int failedLoginAttempts = loginResult.getFailedAttempts();
                    String error;
                    if (failedLoginAttempts > InfusionsoftAuthenticationService.ALLOWED_LOGIN_ATTEMPTS) {
                        error = "login.lockedTooManyFailures";
                    } else {
                        error = "login.failed" + failedLoginAttempts;
                    }
                    throw new BadUsernameOrPasswordAuthenticationException(error);
                case PasswordExpired:
                    throw new PasswordPolicyEnforcementException("login.passwordExpired", "login.passwordExpired", "passwordExpired");
                case Success:
                    return true;
                default:
                    throw new IllegalStateException("Unknown value for loginResult: " + loginResult);
            }
        }
    }
}
