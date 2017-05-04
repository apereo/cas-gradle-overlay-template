package org.apereo.cas.infusionsoft.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apereo.cas.infusionsoft.oauth.mashery.api.domain.MasheryQueryResult;

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
