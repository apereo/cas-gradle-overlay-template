package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryAuthorizationCode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryAuthorizationCode extends MasheryResult {

    private MasheryAuthorizationCode result;

    public MasheryAuthorizationCode getResult() {
        return result;
    }

    public void setResult(MasheryAuthorizationCode result) {
        this.result = result;
    }
}
