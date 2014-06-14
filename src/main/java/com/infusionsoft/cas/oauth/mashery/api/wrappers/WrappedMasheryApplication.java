package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryApplication;

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
