package org.apereo.cas.infusionsoft.oauth.mashery.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasheryAccessToken {

    private String token;
    private String clientId;
    private String tokenType;
    private String grantType;
    private DateTime expires;
    private String scope;
    private String userContext;
    private String extended;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClientId() {
        return clientId;
    }

    @JsonProperty("client_id")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTokenType() {
        return tokenType;
    }

    @JsonProperty("token_type")
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getGrantType() {
        return grantType;
    }

    @JsonProperty("grant_type")
    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public DateTime getExpires() {
        return expires;
    }

    public void setExpires(DateTime expires) {
        this.expires = expires;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUserContext() {
        return userContext;
    }

    @JsonProperty("user_context")
    public void setUserContext(String userContext) {
        this.userContext = userContext;
    }

    public String getExtended() {
        return extended;
    }

    public void setExtended(String extended) {
        this.extended = extended;
    }
}
