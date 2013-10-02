package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryAccessToken;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryAccessToken extends MasheryResult {

    private MasheryAccessToken result;

    public MasheryAccessToken getResult() {
        return result;
    }

    public void setResult(MasheryAccessToken result) {
        this.result = result;
    }
}
