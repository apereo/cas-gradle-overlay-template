package org.apereo.cas.infusionsoft.oauth.mashery.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasheryUserApplication {

    private String id;
    private String name;
    private String client_id;
    private Set<String> accessTokens;

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

    public Set<String> getAccessTokens() {
        return accessTokens;
    }

    @JsonProperty("access_tokens")
    public void setAccessTokens(Set<String> accessTokens) {
        this.accessTokens = accessTokens;
    }
}
