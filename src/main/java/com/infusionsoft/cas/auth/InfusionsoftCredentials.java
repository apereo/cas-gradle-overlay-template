package com.infusionsoft.cas.auth;

import org.jasig.cas.authentication.principal.RememberMeUsernamePasswordCredentials;

public class InfusionsoftCredentials extends RememberMeUsernamePasswordCredentials {
    private String service;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
