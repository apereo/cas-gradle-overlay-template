package org.apereo.cas.infusionsoft.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apereo.cas.api.UserAccountDTO;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Infusionsoft implementation of the authentication handler.
 */
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfusionsoftAuthenticationHandler.class);

    private AppHelper appHelper;

    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    private UserService userService;

    public InfusionsoftAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, int order, InfusionsoftAuthenticationService infusionsoftAuthenticationService, AppHelper appHelper, UserService userService) {
        super(name, servicesManager, principalFactory, order);
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
        this.appHelper = appHelper;
        this.userService = userService;
    }


    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credentials, String originalPassword) throws GeneralSecurityException {
        if (credentials instanceof LetMeInCredentials) {
            return buildHandlerResult(credentials, false);
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

        Map<String, Object> attributes = new HashMap<>();

        if (user != null && user.getId() != null) {
            attributes.put("id", user.getId());
            attributes.put("displayName", user.getFirstName() + " " + user.getLastName());
            attributes.put("firstName", user.getFirstName());
            attributes.put("lastName", user.getLastName());
            attributes.put("email", user.getUsername());
            attributes.put("passwordExpired", expired);

            // We use a query instead of user.getAccounts() so that we only include enabled accounts
            List<UserAccount> accounts = userService.findActiveUserAccounts(user);
            attributes.put("accounts", getAccountsJSON(accounts));
            attributes.put("authorities", user.getAuthorities());

            String principalId = user.getId().toString();
            return this.createHandlerResult(credential, this.principalFactory.createPrincipal(principalId, attributes), null);
        } else {
            LOGGER.error("User is missing on login result");
            throw new IllegalStateException("User not found");
        }

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
        String json;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserAccountDTO[] userAccounts = UserAccountDTO.convertFromCollection(accounts, appHelper);
            objectMapper.writeValue(outputStream, userAccounts);
        } catch (IOException e) {
            LOGGER.error("Error while serializing accounts to JSON", e);
        }

        try {
            json = outputStream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            json = null;
        }

        return json;
    }
}
