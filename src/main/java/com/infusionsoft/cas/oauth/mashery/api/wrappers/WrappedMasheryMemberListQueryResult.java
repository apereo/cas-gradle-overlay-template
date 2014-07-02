package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryMember;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryQueryResult;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryMemberListQueryResult extends MasheryResult {

    private MasheryQueryResult<WrappedMasheryMemberListItem> result;

    public MasheryQueryResult<WrappedMasheryMemberListItem> getResult() {
        return result;
    }

    public void setResult(MasheryQueryResult<WrappedMasheryMemberListItem> result) {
        this.result = result;
    }
}
