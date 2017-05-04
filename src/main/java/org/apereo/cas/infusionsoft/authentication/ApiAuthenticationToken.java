package org.apereo.cas.infusionsoft.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

/**
 * A custom authentication token, which uses the contents of X-Infusionsoft-Global-User-ID as the principal and the contents of
 * X-Infusionsoft-API-Key as the credentials.
 */
public class ApiAuthenticationToken extends AbstractAuthenticationToken {

    private final String apiKey;
    private final Long globalUserId;
    private final String userContext;

    public ApiAuthenticationToken(String apiKey, Long globalUserId, String userContext) {
        super(Collections.EMPTY_SET);
        this.apiKey = apiKey;
        this.globalUserId = globalUserId;
        this.userContext = userContext;
    }

    public Object getCredentials() {
        return apiKey;
    }

    public Object getPrincipal() {
        return getGlobalUserId();
    }

    public String getApiKey() {
        return apiKey;
    }

    public Long getGlobalUserId() {
        return globalUserId;
    }

    public String getUserContext() {
        return userContext;
    }

}
