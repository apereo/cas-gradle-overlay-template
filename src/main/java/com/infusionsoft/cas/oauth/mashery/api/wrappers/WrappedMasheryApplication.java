package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryApplication;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryApplication extends MasheryResult {

    private MasheryApplication result;

    public MasheryApplication getResult() {
        return result;
    }

    public void setResult(MasheryApplication result) {
        this.result = result;
    }
}
