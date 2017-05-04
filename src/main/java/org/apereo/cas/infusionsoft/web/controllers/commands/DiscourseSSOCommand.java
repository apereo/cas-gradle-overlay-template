package org.apereo.cas.infusionsoft.web.controllers.commands;

public class DiscourseSSOCommand {
    private String sso;
    private String sig;

    public String getSso() {
        return sso;
    }

    public void setSso(String sso) {
        this.sso = sso;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }
}
