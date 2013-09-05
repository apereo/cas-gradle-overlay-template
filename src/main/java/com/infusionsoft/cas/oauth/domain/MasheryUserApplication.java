package com.infusionsoft.cas.oauth.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasheryUserApplication {

    private String id;
    private String name;
    private String client_id;
    private Set<String> access_tokens;
    private Set<MasheryAccessToken> tokens;

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

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Set<String> getAccess_tokens() {
        return access_tokens;
    }

    public void setAccess_tokens(Set<String> access_tokens) {
        this.access_tokens = access_tokens;
    }

    public Set<MasheryAccessToken> getTokens() {
        return tokens;
    }

    public void setTokens(Set<MasheryAccessToken> tokens) {
        this.tokens = tokens;
    }
}
