package com.infusionsoft.cas.oauth.services;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.events.UserAccountDeletedEvent;
import com.infusionsoft.cas.oauth.dto.OAuthAccessToken;
import com.infusionsoft.cas.oauth.dto.OAuthApplication;
import com.infusionsoft.cas.oauth.dto.OAuthUserApplication;
import com.infusionsoft.cas.oauth.exceptions.OAuthException;
import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiClientService;
import com.infusionsoft.cas.oauth.mashery.api.domain.*;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.HashSet;
import java.util.Set;

@Service
public class OAuthService implements ApplicationListener<UserAccountDeletedEvent> {

    private static final Logger log = Logger.getLogger(OAuthService.class);

    @Autowired
    private MasheryApiClientService masheryApiClientService;

    @Autowired
    private CrmService crmService;

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(UserAccountDeletedEvent userAccountDeletedEvent) {
        try {
            revokeAccessTokensByUserAccount(userAccountDeletedEvent.getUserAccount());
        } catch (OAuthException e) {
            log.error("Unable to revoke access tokens during account deletion -> " + userAccountDeletedEvent.getUserAccount().toString());
        }
    }

    public OAuthApplication fetchApplication(String clientId, String redirectUri, String responseType) throws OAuthException {
        MasheryOAuthApplication masheryOAuthApplication = masheryApiClientService.fetchOAuthApplication(clientId, redirectUri, responseType);
        MasheryApplication masheryApplication = masheryApiClientService.fetchApplication(masheryOAuthApplication.getId());
        MasheryMember masheryMember = masheryApiClientService.fetchMember(masheryApplication.getUsername());

        return new OAuthApplication(masheryApplication.getName(), masheryApplication.getDescription(), masheryMember.getDisplayName());
    }

    public String createAuthorizationCode(String clientId, String requestedScope, String application, String redirectUri, Long globalUserId, String state) throws OAuthException {
        String scope = requestedScope + "|" + application;
        String userContext = globalUserId + "|" + application;

        userService.validateUserApplication(application);

        MasheryAuthorizationCode masheryAuthorizationCode = masheryApiClientService.createAuthorizationCode(clientId, scope, redirectUri, userContext, state);

        return masheryAuthorizationCode.getUri().getUri();
    }

    /**
     * Creates an access token for the given client and grant
     *
     * @param clientId       The OAuth client_id
     * @param clientSecret   The OAuth client_secret
     * @param grantType      The OAuth grant_type
     * @param requestedScope The request scope which should be the application, i.e. myapp.infusionsoft.com
     * @param globalUserId   The globalUserId of the logged in user
     * @return The created access token or throws exception
     * @throws OAuthException
     */

    public OAuthAccessToken createAccessToken(String clientId, String clientSecret, String grantType, String requestedScope, String application, Long globalUserId) throws OAuthException {
        String scope = StringUtils.defaultString(requestedScope) + "|" + application;
        String userContext = globalUserId + "|" + application;

        userService.validateUserApplication(application);

        MasheryCreateAccessTokenResponse masheryCreateAccessTokenResponse = masheryApiClientService.createAccessToken(clientId, clientSecret, grantType, scope, userContext);

        return new OAuthAccessToken(masheryCreateAccessTokenResponse.getAccess_token(), masheryCreateAccessTokenResponse.getToken_type(), masheryCreateAccessTokenResponse.getExpires_in(), masheryCreateAccessTokenResponse.getRefresh_token(), masheryCreateAccessTokenResponse.getScope());
    }

    public Boolean revokeAccessToken(String clientId, String accessToken) throws OAuthException {
        return masheryApiClientService.revokeAccessToken(clientId, accessToken);
    }

    public OAuthAccessToken fetchAccessToken(String accessToken) throws OAuthException {
        MasheryAccessToken masheryAccessToken = masheryApiClientService.fetchAccessToken(accessToken);

        return new OAuthAccessToken(masheryAccessToken.getToken(), masheryAccessToken.getToken_type(), masheryAccessToken.getExpires(), null, masheryAccessToken.getScope());
    }

    public Set<OAuthUserApplication> fetchUserApplicationsByUserAccount(UserAccount userAccount) throws OAuthException {
        Set<OAuthUserApplication> retVal = new HashSet<OAuthUserApplication>();

        if (userAccount != null) {
            User user = userAccount.getUser();
            if (user != null) {
                String userContext = user.getId() + "|" + crmService.buildCrmHostName(userAccount.getAppName());
                Set<MasheryUserApplication> masheryUserApplications = masheryApiClientService.fetchUserApplicationsByUserContext(userContext, TokenStatus.Active);

                for (MasheryUserApplication masheryUserApplication : masheryUserApplications) {
                    Set<OAuthAccessToken> accessTokens = new HashSet<OAuthAccessToken>();

                    for (MasheryAccessToken masheryAccessToken : masheryUserApplication.getTokens()) {
                        accessTokens.add(new OAuthAccessToken(masheryAccessToken.getToken(), masheryAccessToken.getToken_type(), masheryAccessToken.getExpires(), null, masheryAccessToken.getScope()));
                    }

                    OAuthUserApplication oAuthUserApplication = new OAuthUserApplication(masheryUserApplication.getId(), masheryUserApplication.getName(), masheryUserApplication.getClient_id(), accessTokens);
                    retVal.add(oAuthUserApplication);
                }

            }
        }

        return retVal;
    }

    public boolean revokeAccessTokensByUserAccount(UserAccount account) throws OAuthException {
        boolean revokeSuccessful = true;

        Set<OAuthUserApplication> masheryUserApplications = this.fetchUserApplicationsByUserAccount(account);
        for (OAuthUserApplication masheryUserApplication : masheryUserApplications) {
            for (OAuthAccessToken token : masheryUserApplication.getAccessTokens()) {
                try {
                    revokeSuccessful = masheryApiClientService.revokeAccessToken(masheryUserApplication.getClientId(), token.getAccessToken()) && revokeSuccessful;
                } catch (RestClientException e) {
                    log.error("Unable to revoke access token for app=" + account.getAppName() + " clientId=" + masheryUserApplication.getClientId() + " token=" + token, e);
                    revokeSuccessful = false;
                }
            }
        }

        return revokeSuccessful;
    }

    public boolean revokeAccessTokensByUserAccount(UserAccount account, String applicationId) throws OAuthException {
        boolean revokeSuccessful = true;

        Set<OAuthUserApplication> oAuthUserApplications = this.fetchUserApplicationsByUserAccount(account);
        for (OAuthUserApplication oAuthUserApplication : oAuthUserApplications) {
            if(applicationId.equals(oAuthUserApplication.getId())) {
                for (OAuthAccessToken token : oAuthUserApplication.getAccessTokens()) {
                    try {
                        revokeSuccessful = masheryApiClientService.revokeAccessToken(oAuthUserApplication.getClientId(), token.getAccessToken()) && revokeSuccessful;
                    } catch (RestClientException e) {
                        log.error("Unable to revoke access token for app=" + account.getAppName() + " clientId=" + oAuthUserApplication.getClientId() + " token=" + token, e);
                        revokeSuccessful = false;
                    }
                }
            }
        }

        return revokeSuccessful;
    }
}
