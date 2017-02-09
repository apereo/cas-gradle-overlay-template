package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.authentication.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;

/**
 * Infusionsoft implementation of the authentication handler.
 */
@Component
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    PasswordEncoder passwordEncoder;

    protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credentials) throws GeneralSecurityException {
        if (credentials instanceof LetMeInCredentials) {
            return this.createHandlerResult(credentials, this.principalFactory.createPrincipal(credentials.getUsername()), null);
        } else {
            LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(credentials.getUsername(), credentials.getPassword());

            switch (loginResult.getLoginStatus()) {
                case AccountLocked:
                    throw new AccountLockedException();
                case BadPassword:
                case DisabledUser:
                case NoSuchUser:
                case OldPassword:
                    int failedLoginAttempts = loginResult.getFailedAttempts();
                    if (failedLoginAttempts > InfusionsoftAuthenticationService.ALLOWED_LOGIN_ATTEMPTS) {
                        throw new AccountLockedException();
                    } else if (failedLoginAttempts == 0) { // This happens if an old password is matched
                        throw new FailedLoginException();
                    } else {
                        try {
                            throw ((FailedLoginException) Class.forName("com.infusionsoft.cas.exceptions.FailedLoginException" + failedLoginAttempts).newInstance());
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                            throw new FailedLoginException();
                        }
                    }
                case PasswordExpired:
                    throw new AccountPasswordMustChangeException("login.passwordExpired");
                case Success:
                    return this.createHandlerResult(credentials, this.principalFactory.createPrincipal(credentials.getUsername()), null);
                default:
                    throw new IllegalStateException("Unknown value for loginResult: " + loginResult);
            }
        }
    }
}
