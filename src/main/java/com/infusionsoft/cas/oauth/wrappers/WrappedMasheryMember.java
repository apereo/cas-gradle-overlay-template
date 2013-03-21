package com.infusionsoft.cas.oauth.wrappers;

import com.infusionsoft.cas.oauth.domain.MasheryMember;
import com.infusionsoft.cas.oauth.MasheryResult;
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
