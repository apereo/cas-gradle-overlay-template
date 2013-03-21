package com.infusionsoft.cas.oauth.wrappers;

import com.infusionsoft.cas.oauth.MasheryResult;
import com.infusionsoft.cas.oauth.domain.MasheryAuthorizationCode;
import com.infusionsoft.cas.oauth.domain.MasheryMember;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryAuthorizationCode extends MasheryResult {

    private MasheryAuthorizationCode result;

    public MasheryAuthorizationCode getResult() {
        return result;
    }

    public void setResult(MasheryAuthorizationCode result) {
        this.result = result;
    }
}
