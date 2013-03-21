package com.infusionsoft.cas.oauth.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

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
    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
