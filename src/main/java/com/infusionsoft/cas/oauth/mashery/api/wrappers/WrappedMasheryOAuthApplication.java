package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryOAuthApplication;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryOAuthApplication extends MasheryResult {

    private MasheryOAuthApplication result;

    public MasheryOAuthApplication getResult() {
        return result;
    }

    public void setResult(MasheryOAuthApplication result) {
        this.result = result;
    }
}
