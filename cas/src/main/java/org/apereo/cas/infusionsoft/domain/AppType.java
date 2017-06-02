package org.apereo.cas.infusionsoft.domain;

/**
 * Constants for certain app type strings that are sometimes sent across the wire in the CAS response.
 */
public enum AppType {

    COMMUNITY(true, false, false),
    CRM(true, true, true),
    CUSTOMERHUB(true, true, false),
    MARKETPLACE(false, false, false);

    private boolean linkageAllowed;
    private boolean aliasable;
    private boolean accessTokensAllowed;

    AppType(boolean linkageAllowed, boolean aliasable, boolean accessTokensAllowed) {
        this.linkageAllowed = linkageAllowed;
        this.aliasable = aliasable;
        this.accessTokensAllowed = accessTokensAllowed;
    }

    public boolean isLinkageAllowed() {
        return linkageAllowed;
    }

    public boolean isAliasable() {
        return aliasable;
    }

    public boolean isAccessTokensAllowed() {
        return accessTokensAllowed;
    }
}
