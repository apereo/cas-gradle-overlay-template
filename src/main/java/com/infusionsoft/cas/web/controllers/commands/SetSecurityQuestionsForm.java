package com.infusionsoft.cas.web.controllers.commands;

import java.io.Serializable;

public class SetSecurityQuestionsForm implements Serializable {
    private long securityQuestionId;
    private String response;
    private boolean skip = false;

    public long getSecurityQuestionId() {
        return securityQuestionId;
    }

    public void setSecurityQuestionId(long securityQuestionId) {
        this.securityQuestionId = securityQuestionId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
