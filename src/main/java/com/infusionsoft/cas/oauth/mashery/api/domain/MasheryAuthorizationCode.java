package com.infusionsoft.cas.oauth.mashery.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
