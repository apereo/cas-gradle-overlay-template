package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryAccessToken;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryCreateAccessTokenResponse;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
