package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryCreateAccessTokenResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryCreateAccessTokenResponse extends MasheryResult {

    private MasheryCreateAccessTokenResponse result;

    public MasheryCreateAccessTokenResponse getResult() {
        return result;
    }

    public void setResult(MasheryCreateAccessTokenResponse result) {
        this.result = result;
    }
}
