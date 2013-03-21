package com.infusionsoft.cas.oauth.wrappers;

import com.infusionsoft.cas.oauth.domain.MasheryApplication;
import com.infusionsoft.cas.oauth.MasheryResult;
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
