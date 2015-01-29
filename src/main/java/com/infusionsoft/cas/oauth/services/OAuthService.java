package com.infusionsoft.cas.oauth.services;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.events.UserAccountRemovedEvent;
import com.infusionsoft.cas.oauth.dto.OAuthAccessToken;
import com.infusionsoft.cas.oauth.dto.OAuthApplication;
import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import com.infusionsoft.cas.oauth.dto.OAuthUserApplication;
import com.infusionsoft.cas.oauth.exceptions.OAuthAccessDeniedException;
import com.infusionsoft.cas.oauth.exceptions.OAuthException;
import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiClientService;
import com.infusionsoft.cas.oauth.mashery.api.domain.*;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.HashSet;
import java.util.Set;

@Service
public class OAuthService implements ApplicationListener<UserAccountRemovedEvent> {

    private static final Logger log = Logger.getLogger(OAuthService.class);
    private static final String TRUSTED_INTERNAL_SYSTEM_ROLE = "Trusted Internal System";

    @Autowired
    private MasheryApiClientService masheryApiClientService;

    @Autowired
    private CrmService crmService;

    @Autowired
    private UserService userService;

    @Value("${mashery.api.crm.service.key}")
    private String crmServiceKey;

    @Override
    public void onApplicationEvent(UserAccountRemovedEvent userAccountRemovedEvent) {
        try {
            revokeAccessTokensByUserAccount(crmServiceKey, userAccountRemovedEvent.getUserAccount());
        } catch (OAuthException e) {
            log.error("Unable to revoke access tokens during account deletion -> " + userAccountRemovedEvent.getUserAccount().toString());
        }
    }

    public OAuthApplication fetchApplication(String serviceKey, String clientId, String redirectUri, String responseType) throws OAuthException {
        MasheryOAuthApplication masheryOAuthApplication = masheryApiClientService.fetchOAuthApplication(serviceKey, clientId, redirectUri, responseType);
        MasheryApplication masheryApplication = masheryApiClientService.fetchApplication(masheryOAuthApplication.getId());
        MasheryMember masheryMember = masheryApiClientService.fetchMember(masheryApplication.getUsername());

        Set<String> roles = new HashSet<String>();
        for (MasheryRole masheryRole : masheryMember.getRoles()) {
            roles.add(masheryRole.getName());
        }

        return new OAuthApplication(masheryApplication.getName(), masheryApplication.getDescription(), masheryMember.getDisplayName(), roles);
    }

    public String createAuthorizationCode(String serviceKey, String clientId, String requestedScope, String application, String redirectUri, Long globalUserId, String state) throws OAuthException {
        String scope = requestedScope + "|" + application;
        String userContext = globalUserId + "|" + application;

        if(!userService.validateUserApplication(application) ) {
            throw new OAuthAccessDeniedException();
        }

        MasheryAuthorizationCode masheryAuthorizationCode = masheryApiClientService.createAuthorizationCode(serviceKey, clientId, scope, redirectUri, userContext, state);

        return masheryAuthorizationCode.getUri().getUri();
    }

    /**
     * Creates an access token for the given client and grant
     *
     * @param providedServiceKey     The Mashery Service Key
     * @param clientId       The OAuth client_id
     * @param clientSecret   The OAuth client_secret
     * @param grantType      The OAuth grant_type
     * @param requestedScope The request scope which should be the application, i.e. myapp.infusionsoft.com
     * @param userId         The user identifier, which is either a globalUserId or the anonymous-UUID tracking code
     * @return The created access token or throws exception
     * @throws OAuthException
     */
    public OAuthAccessToken createAccessToken(String providedServiceKey, String clientId, String clientSecret, String grantType, String requestedScope, String application, String userId, String refreshToken) throws OAuthException {
        String scope = StringUtils.isBlank(requestedScope) && StringUtils.isBlank(application) ? "" : StringUtils.defaultString(requestedScope) + "|" + StringUtils.defaultString(application);
        String userContext = StringUtils.isBlank(userId) && StringUtils.isBlank(application) ? "" : StringUtils.defaultString(userId) + "|" + StringUtils.defaultString(application);

        /**
         * Mashery does not support extend grants, so we are faking it by using a password
         */
        if(isExtendedGrantType(grantType)) {
            grantType = OAuthGrantType.RESOURCE_OWNER_CREDENTIALS.getValue();
        }

        MasheryCreateAccessTokenResponse masheryCreateAccessTokenResponse = masheryApiClientService.createAccessToken(providedServiceKey, clientId, clientSecret, grantType, scope, userContext, refreshToken);

        return new OAuthAccessToken(masheryCreateAccessTokenResponse.getAccess_token(), masheryCreateAccessTokenResponse.getToken_type(), masheryCreateAccessTokenResponse.getExpires_in(), masheryCreateAccessTokenResponse.getRefresh_token(), masheryCreateAccessTokenResponse.getScope());
    }

    public Boolean revokeAccessToken(String serviceKey, String clientId, String accessToken) throws OAuthException {
        return masheryApiClientService.revokeAccessToken(serviceKey, clientId, accessToken);
    }

    public Set<OAuthUserApplication> fetchUserApplicationsByUserAccount(String serviceKey, UserAccount userAccount) throws OAuthException {
        Set<OAuthUserApplication> retVal = new HashSet<OAuthUserApplication>();

        if (userAccount != null) {
            User user = userAccount.getUser();
            if (user != null) {
                String userContext = user.getId() + "|" + crmService.buildCrmHostName(userAccount.getAppName());
                Set<MasheryUserApplication> masheryUserApplications = masheryApiClientService.fetchUserApplicationsByUserContext(serviceKey, userContext);

                for (MasheryUserApplication masheryUserApplication : masheryUserApplications) {
                    OAuthUserApplication oAuthUserApplication = new OAuthUserApplication(masheryUserApplication.getId(), masheryUserApplication.getName(), masheryUserApplication.getClient_id(), masheryUserApplication.getAccessTokens());
                    retVal.add(oAuthUserApplication);
                }

            }
        }

        return retVal;
    }

    public boolean revokeAccessTokensByUserAccount(String serviceKey, UserAccount account) throws OAuthException {
        boolean revokeSuccessful = true;

        Set<OAuthUserApplication> masheryUserApplications = this.fetchUserApplicationsByUserAccount(serviceKey, account);
        for (OAuthUserApplication masheryUserApplication : masheryUserApplications) {
            for (String token : masheryUserApplication.getAccessTokens()) {
                try {
                    revokeSuccessful = masheryApiClientService.revokeAccessToken(serviceKey, masheryUserApplication.getClientId(), token) && revokeSuccessful;
                } catch (RestClientException e) {
                    log.error("Unable to revoke access token for app=" + account.getAppName() + " clientId=" + masheryUserApplication.getClientId() + " token=" + token, e);
                    revokeSuccessful = false;
                }
            }
        }

        return revokeSuccessful;
    }

    public boolean revokeAccessTokensByUserAccount(String serviceKey, UserAccount account, String applicationId) throws OAuthException {
        boolean revokeSuccessful = true;

        Set<OAuthUserApplication> oAuthUserApplications = this.fetchUserApplicationsByUserAccount(serviceKey, account);
        for (OAuthUserApplication oAuthUserApplication : oAuthUserApplications) {
            if (applicationId.equals(oAuthUserApplication.getId())) {
                for (String token : oAuthUserApplication.getAccessTokens()) {
                    try {
                        revokeSuccessful = masheryApiClientService.revokeAccessToken(serviceKey, oAuthUserApplication.getClientId(), token) && revokeSuccessful;
                    } catch (RestClientException e) {
                        log.error("Unable to revoke access token for app=" + account.getAppName() + " clientId=" + oAuthUserApplication.getClientId() + " token=" + token, e);
                        revokeSuccessful = false;
                    }
                }
            }
        }

        return revokeSuccessful;
    }

    public boolean isExtendedGrantType(String grantType) {
        return OAuthGrantType.EXTENDED_TRUSTED.isValueEqual(grantType) || OAuthGrantType.EXTENDED_TICKET_GRANTING_TICKET.isValueEqual(grantType);
    }

    public boolean isClientAuthorizedForTrustedGrantType(String clientId) throws OAuthException {
        MasheryMember masheryMember = masheryApiClientService.fetchMemberByClientId(clientId);
        for(MasheryRole masheryRole : masheryMember.getRoles()) {
            if(TRUSTED_INTERNAL_SYSTEM_ROLE.equals(masheryRole.getName())) {
                return true;
            }
        }

        return false;
    }
}
