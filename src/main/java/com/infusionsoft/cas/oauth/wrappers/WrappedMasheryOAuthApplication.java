package com.infusionsoft.cas.oauth.wrappers;

import com.infusionsoft.cas.oauth.domain.MasheryOAuthApplication;
import com.infusionsoft.cas.oauth.MasheryResult;
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
