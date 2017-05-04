package org.apereo.cas.infusionsoft.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apereo.cas.infusionsoft.oauth.mashery.api.domain.MasheryMember;
import org.apereo.cas.infusionsoft.oauth.mashery.api.domain.MasheryQueryResult;

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
