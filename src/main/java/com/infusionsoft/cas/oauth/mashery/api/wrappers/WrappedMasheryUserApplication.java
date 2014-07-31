package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryUserApplication;

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
