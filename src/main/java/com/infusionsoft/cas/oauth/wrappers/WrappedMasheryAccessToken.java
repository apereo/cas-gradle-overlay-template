package com.infusionsoft.cas.oauth.wrappers;

import com.infusionsoft.cas.oauth.MasheryResult;
import com.infusionsoft.cas.oauth.domain.MasheryAccessToken;
import com.infusionsoft.cas.oauth.domain.MasheryUserApplication;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Set;

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
