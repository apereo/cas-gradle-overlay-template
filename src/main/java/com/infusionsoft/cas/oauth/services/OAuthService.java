package com.infusionsoft.cas.oauth.services;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.dto.OAuthAccessToken;
import com.infusionsoft.cas.oauth.dto.OAuthApplication;
import com.infusionsoft.cas.oauth.dto.OAuthUserApplication;
import com.infusionsoft.cas.oauth.exceptions.OAuthAccessDeniedException;
import com.infusionsoft.cas.oauth.exceptions.OAuthException;
import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiClientService;
import com.infusionsoft.cas.oauth.mashery.api.domain.*;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OAuthService {

    private static final Logger log = Logger.getLogger(OAuthService.class);

    @Autowired
    private MasheryApiClientService masheryApiClientService;

    @Autowired
    private CrmService crmService;

    @Autowired
    private UserService userService;

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
    public MasheryCreateAccessTokenResponse createAccessToken(String clientId, String clientSecret, String grantType, String requestedScope, Long globalUserId) throws OAuthException {
        return createAccessToken(clientId, clientSecret, grantType, requestedScope, requestedScope, globalUserId);
    }

    public MasheryCreateAccessTokenResponse createAccessToken(String clientId, String clientSecret, String grantType, String requestedScope, String application, Long globalUserId) throws OAuthException {
        String scope = requestedScope + "|" + application;
        String userContext = globalUserId + "|" + application;

        userService.validateUserApplication(application);

        return masheryApiClientService.createAccessToken(clientId, clientSecret, grantType, scope, userContext);
    }

    public Boolean revokeAccessToken(String clientId, String accessToken) throws OAuthException {
        return masheryApiClientService.revokeAccessToken(clientId, accessToken);
    }

    public MasheryAccessToken fetchAccessToken(String accessToken) throws OAuthException {
        return masheryApiClientService.fetchAccessToken(accessToken);
    }

//    public Set<MasheryUserApplication> fetchUserApplicationsByUserAccount(UserAccount userAccount) throws OAuthException {
//        Set<MasheryUserApplication> masheryUserApplications = new HashSet<MasheryUserApplication>();
//
//        if (userAccount != null) {
//            User user = userAccount.getUser();
//            if (user != null) {
//                String userContext = user.getId() + "|" + crmService.buildCrmHostName(userAccount.getAppName());
//                masheryUserApplications = masheryApiClientService.fetchUserApplicationsByUserContext(userContext, TokenStatus.Active);
//            }
//        }
//
//        return masheryUserApplications;
//    }

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
                        accessTokens.add(new OAuthAccessToken(masheryAccessToken.getToken(), masheryAccessToken.getToken_type(), NumberUtils.createInteger(masheryAccessToken.getExpires()), null, masheryAccessToken.getScope()));
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
                    revokeSuccessful = masheryApiClientService.revokeAccessToken(masheryUserApplication.getClientId(), token.getAccess_token()) && revokeSuccessful;
                } catch (RestClientException e) {
                    log.error("Unable to revoke access token for app=" + account.getAppName() + " clientId=" + masheryUserApplication.getClientId() + " token=" + token, e);
                    revokeSuccessful = false;
                }
            }
        }

        return revokeSuccessful;
    }
}
