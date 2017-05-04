package org.apereo.cas.infusionsoft.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apereo.cas.infusionsoft.oauth.mashery.api.domain.MasheryAccessToken;

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
