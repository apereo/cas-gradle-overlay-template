package com.infusionsoft.cas.oauth.dto;

import java.util.Set;

public class OAuthUserApplication {

    private String id;
    private String name;
    private String clientId;
    private Set<OAuthAccessToken> accessTokens;

    public OAuthUserApplication(String id, String name, String clientId, Set<OAuthAccessToken> accessTokens) {
        this.id = id;
        this.name = name;
        this.clientId = clientId;
        this.accessTokens = accessTokens;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Set<OAuthAccessToken> getAccessTokens() {
        return accessTokens;
    }

    public void setAccessTokens(Set<OAuthAccessToken> accessTokens) {
        this.accessTokens = accessTokens;
    }
}
