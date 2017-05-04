package org.apereo.cas.infusionsoft.oauth.mashery.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasheryUri {

    private String uri;
    private String state;

    public MasheryUri() {
    }

    public MasheryUri(String uri, String state) {
        this.uri = uri;
        this.state = state;
    }

    @JsonProperty("redirect_uri")
    public String getUri() {
        return uri;
    }

    @JsonProperty("redirect_uri")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getState() {
        return state;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public void setState(String state) {
        this.state = state;
    }
}
