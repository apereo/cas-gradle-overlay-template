package com.infusionsoft.cas.oauth.services;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.exceptions.OAuthException;
import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiClientService;
import com.infusionsoft.cas.oauth.mashery.api.domain.*;
import com.infusionsoft.cas.services.CrmService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.HashSet;
import java.util.Set;

@Service
public class OAuthService {

    private static final Logger log = Logger.getLogger(OAuthService.class);

    @Autowired
    private MasheryApiClientService masheryApiClientService;

    @Autowired
    private CrmService crmService;

    public MasheryOAuthApplication fetchOAuthApplication(String clientId, String redirectUri, String responseType) throws OAuthException {
        return masheryApiClientService.fetchOAuthApplication(clientId, redirectUri, responseType);
    }

    public MasheryApplication fetchApplication(Integer id) throws OAuthException {
        return masheryApiClientService.fetchApplication(id);
    }

    public MasheryMember fetchMember(String username) throws OAuthException {
        return masheryApiClientService.fetchMember(username);
    }

    public MasheryAuthorizationCode createAuthorizationCode(String clientId, String requestedScope, String application, String redirectUri, Long globalUserId, String state) throws OAuthException {
        String scope = requestedScope + "|" + application;
        String userContext = globalUserId + "|" + application;

        return masheryApiClientService.createAuthorizationCode(clientId, scope, redirectUri, userContext, state);
    }

    public Boolean revokeAccessToken(String clientId, String accessToken) throws OAuthException {
        return masheryApiClientService.revokeAccessToken(clientId, accessToken);
    }

    public MasheryAccessToken fetchAccessToken(String accessToken) throws OAuthException {
        return masheryApiClientService.fetchAccessToken(accessToken);
    }

    public Set<MasheryUserApplication> fetchUserApplicationsByUserAccount(UserAccount userAccount) throws OAuthException {
        Set<MasheryUserApplication> masheryUserApplications = new HashSet<MasheryUserApplication>();

        if (userAccount != null) {
            User user = userAccount.getUser();
            if (user != null) {
                String userContext = user.getId() + "|" + crmService.buildCrmHostName(userAccount.getAppName());
                masheryUserApplications = masheryApiClientService.fetchUserApplicationsByUserContext(userContext, TokenStatus.Active);
            }
        }

        return masheryUserApplications;
    }

    public boolean revokeAccessTokensByUserAccount(UserAccount account) throws OAuthException {
        boolean revokeSuccessful = true;

        Set<MasheryUserApplication> masheryUserApplications = this.fetchUserApplicationsByUserAccount(account);
        for (MasheryUserApplication masheryUserApplication : masheryUserApplications) {
            for (String token : masheryUserApplication.getAccess_tokens()) {
                try {
                    revokeSuccessful = masheryApiClientService.revokeAccessToken(masheryUserApplication.getClient_id(), token) && revokeSuccessful;
                } catch (RestClientException e) {
                    log.error("Unable to revoke access token for app=" + account.getAppName() + " clientId=" + masheryUserApplication.getClient_id() + " token=" + token, e);
                    revokeSuccessful = false;
                }
            }
        }

        return revokeSuccessful;
    }
}
