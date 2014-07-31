package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryMember;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryQueryResult;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryMemberQueryResult extends MasheryResult {

    private MasheryQueryResult<MasheryMember> result;

    public MasheryQueryResult<MasheryMember> getResult() {
        return result;
    }

    public void setResult(MasheryQueryResult<MasheryMember> result) {
        this.result = result;
    }
}
