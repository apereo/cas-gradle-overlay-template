package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.exceptions.PasswordPolicyEnforcementException;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
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
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    PasswordEncoder passwordEncoder;

    protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials credentials) throws AuthenticationException {
        //TODO: upgrade
        if (credentials instanceof LetMeInCredentials) {
            return true;
        } else {
            LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(credentials.getUsername(), credentials.getPassword());

            switch (loginResult.getLoginStatus()) {
                case AccountLocked:
                    throw new BlockedCredentialsAuthenticationException("login.lockedTooManyFailures");
                case BadPassword:
                case DisabledUser:
                case NoSuchUser:
                case OldPassword:
                    int failedLoginAttempts = loginResult.getFailedAttempts();
                    String error;
                    if (failedLoginAttempts > InfusionsoftAuthenticationService.ALLOWED_LOGIN_ATTEMPTS) {
                        error = "login.lockedTooManyFailures";
                    } else if (failedLoginAttempts == 0) { // This happens if an old password is matched
                        error = "login.failed1";
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
