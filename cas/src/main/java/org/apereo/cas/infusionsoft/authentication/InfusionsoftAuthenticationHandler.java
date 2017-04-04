package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;

/**
 * Infusionsoft implementation of the authentication handler.
 */
@Component
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    public InfusionsoftAuthenticationHandler() {
        super("Infusionsoft Authentication Handler", null, null, null);
    }

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credentials, String originalPassword) throws GeneralSecurityException {
        if (credentials instanceof LetMeInCredentials) {
            return this.createHandlerResult(credentials, this.principalFactory.createPrincipal(credentials.getUsername()), null);
        } else {
            LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(credentials.getUsername(), originalPassword);

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
                    throw new CredentialExpiredException("login.passwordExpired");
                case Success:
                    return this.createHandlerResult(credentials, this.principalFactory.createPrincipal(credentials.getUsername()), null);
                default:
                    throw new IllegalStateException("Unknown value for loginResult: " + loginResult);
            }
        }
    }
}
