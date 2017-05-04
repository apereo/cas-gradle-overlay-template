package org.apereo.cas.infusionsoft.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apereo.cas.infusionsoft.oauth.mashery.api.domain.MasheryMember;

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
