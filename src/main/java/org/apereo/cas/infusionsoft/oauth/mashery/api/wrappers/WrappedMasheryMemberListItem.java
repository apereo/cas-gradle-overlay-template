package org.apereo.cas.infusionsoft.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apereo.cas.infusionsoft.oauth.mashery.api.domain.MasheryMember;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WrappedMasheryMemberListItem {

    private MasheryMember member;

    public MasheryMember getMember() {
        return member;
    }

    public void setMember(MasheryMember member) {
        this.member = member;
    }
}
