package com.infusionsoft.cas.web.controllers.commands;

import com.infusionsoft.cas.domain.SecurityQuestionResponse;

import java.util.List;

public class EditProfileForm {
    private String firstName;
    private String lastName;
    private List<SecurityQuestionResponse> securityQuestionResponses;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<SecurityQuestionResponse> getSecurityQuestionResponses() {
        return securityQuestionResponses;
    }

    public void setSecurityQuestionResponses(List<SecurityQuestionResponse> securityQuestionResponses) {
        this.securityQuestionResponses = securityQuestionResponses;
    }
}
