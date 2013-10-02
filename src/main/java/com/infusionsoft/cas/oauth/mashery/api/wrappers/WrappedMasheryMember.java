package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryMember;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
