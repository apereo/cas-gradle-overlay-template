package org.apereo.cas.infusionsoft.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apereo.cas.api.UserAccountDTO;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.FailedLoginException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * Infusionsoft implementation of the authentication handler.
 */
@Component
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfusionsoftAuthenticationHandler.class);

    public InfusionsoftAuthenticationHandler() {
        super("Infusionsoft Authentication Handler", null, null, null);
    }

    @Autowired
    private AppHelper appHelper;

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private UserService userService;

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
                    return buildHandlerResult(loginResult, credentials);
                default:
                    throw new IllegalStateException("Unknown value for loginResult: " + loginResult);
            }
        }
    }

    private HandlerResult buildHandlerResult(LoginResult loginResult, UsernamePasswordCredential credential) {
        User user = loginResult.getUser();

        Map<String, Object> attributes = new HashMap<>();

        if (user != null) {
            attributes.put("id", user.getId());
            attributes.put("displayName", user.getFirstName() + " " + user.getLastName());
            attributes.put("firstName", user.getFirstName());
            attributes.put("lastName", user.getLastName());
            attributes.put("email", user.getUsername());

            // We use a query instead of user.getAccounts() so that we only include enabled accounts
            List<UserAccount> accounts = userService.findActiveUserAccounts(user);
            attributes.put("accounts", getAccountsJSON(accounts));
            attributes.put("authorities", user.getAuthorities());
        } else {
            LOGGER.error("User is missing on login result");
        }

        return this.createHandlerResult(credential, this.principalFactory.createPrincipal(credential.getUsername(), attributes) , null);
    }

    /**
     * *************************************************************************************************
     * * * WARNING * * *
     * If the format/content of this JSON ever changes in a way that affects parsing on the receiving end,
     * the TICKETGRANTINGTICKET table needs to be completely cleared, since the old tickets stored there
     * will still have the old format
     * **************************************************************************************************
     */
    private String getAccountsJSON(List<UserAccount> accounts) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserAccountDTO[] userAccounts = UserAccountDTO.convertFromCollection(accounts, appHelper);
            objectMapper.writeValue(outputStream, userAccounts);
        } catch (IOException e) {
            LOGGER.error("Error while serializing accounts to JSON", e);
        }
        return outputStream.toString();
    }
}
