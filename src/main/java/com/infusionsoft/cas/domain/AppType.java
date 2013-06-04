package com.infusionsoft.cas.domain;

/**
 * Constants for certain app type strings that are sometimes sent across the wire in the CAS response.
 */
public enum AppType {

    CAS(false),
    COMMUNITY(true),
    CRM(true),
    CUSTOMERHUB(true),
    MARKETPLACE(false);

    private boolean linkageAllowed;

    AppType(boolean linkageAllowed) {
        this.linkageAllowed = linkageAllowed;
    }

    public boolean isLinkageAllowed() {
        return linkageAllowed;
    }
}
