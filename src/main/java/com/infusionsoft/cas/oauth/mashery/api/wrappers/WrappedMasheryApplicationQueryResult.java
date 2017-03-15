package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryApplication;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryQueryResult;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryApplicationQueryResult extends MasheryResult {

    private MasheryQueryResult<MasheryApplication> result;

    public MasheryQueryResult<MasheryApplication> getResult() {
        return result;
    }

    public void setResult(MasheryQueryResult<MasheryApplication> result) {
        this.result = result;
    }
}
