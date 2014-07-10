package com.infusionsoft.cas.oauth.mashery.api.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryMember;

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
