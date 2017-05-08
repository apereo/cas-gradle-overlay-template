package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * Infusionsoft implementation of the authentication handler.
 */
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfusionsoftAuthenticationHandler.class);

    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private UserService userService;
    private MessageSource messageSource;

    public InfusionsoftAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, int order, InfusionsoftAuthenticationService infusionsoftAuthenticationService, UserService userService, MessageSource messageSource) {
        super(name, servicesManager, principalFactory, order);
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credentials, String originalPassword) throws GeneralSecurityException {
        if (credentials instanceof LetMeInCredentials) {
            return buildHandlerResult(credentials, false);
        } else {
            LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(credentials.getUsername(), originalPassword);

            switch (loginResult.getLoginStatus()) {
                case AccountLocked:
                    throw new AccountLockedException(messageSource.getMessage("login.lockedTooManyFailures", null, LocaleContextHolder.getLocale()));
                case BadPassword:
                case DisabledUser:
                case NoSuchUser:
                case OldPassword:
                    int failedLoginAttempts = loginResult.getFailedAttempts();
                    String errorCode;
                    if (failedLoginAttempts > InfusionsoftAuthenticationService.ALLOWED_LOGIN_ATTEMPTS) {
                        throw new AccountLockedException(messageSource.getMessage("login.lockedTooManyFailures", null, LocaleContextHolder.getLocale()));
                    } else if (failedLoginAttempts == 0) { // This happens if an old password is matched
                        errorCode = "login.failed1";
                    } else {
                        errorCode = "login.failed" + failedLoginAttempts;
                    }
                    throw new FailedLoginException(messageSource.getMessage(errorCode, null, LocaleContextHolder.getLocale()));
                case PasswordExpired:
                    return buildHandlerResult(credentials, true);
                case Success:
                    return buildHandlerResult(credentials, false);
                default:
                    throw new IllegalStateException("Unknown value for loginResult: " + loginResult);
            }
        }
    }

    private HandlerResult buildHandlerResult(UsernamePasswordCredential credential, boolean expired) {
        User user = userService.loadUser(credential.getUsername());

        if (user != null && user.getId() != null) {
            Map<String, Object> attributes = userService.createAttributeMapForUser(user);
            attributes.put("passwordExpired", expired);

            String principalId = user.getId().toString();
            return this.createHandlerResult(credential, this.principalFactory.createPrincipal(principalId, attributes), null);
        } else {
            LOGGER.error("User is missing on login result");
            throw new IllegalStateException("User not found");
        }
    }

}
