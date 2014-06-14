package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryMember;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryMember extends MasheryResult {

    private MasheryMember result;

    public MasheryMember getResult() {
        return result;
    }

    public void setResult(MasheryMember result) {
        this.result = result;
    }
}
