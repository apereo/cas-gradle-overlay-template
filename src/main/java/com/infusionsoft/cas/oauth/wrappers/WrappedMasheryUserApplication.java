package com.infusionsoft.cas.oauth.wrappers;

import com.infusionsoft.cas.oauth.MasheryResult;
import com.infusionsoft.cas.oauth.domain.MasheryApplication;
import com.infusionsoft.cas.oauth.domain.MasheryUserApplication;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryUserApplication extends MasheryResult {

    private Set<MasheryUserApplication> result;

    public Set<MasheryUserApplication> getResult() {
        return result;
    }

    public void setResult(Set<MasheryUserApplication> result) {
        this.result = result;
    }
}
