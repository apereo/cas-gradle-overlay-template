package com.infusionsoft.cas.oauth.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasheryAuthorizationCode {

    private String code;
    private MasheryUri uri;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MasheryUri getUri() {
        return uri;
    }

    public void setUri(MasheryUri uri) {
        this.uri = uri;
    }
}
