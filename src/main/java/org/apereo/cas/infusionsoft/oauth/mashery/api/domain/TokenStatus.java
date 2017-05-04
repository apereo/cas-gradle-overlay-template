package org.apereo.cas.infusionsoft.oauth.mashery.api.domain;

public enum TokenStatus {
    Active("active"),
    Inactive("inactive"),
    All("all");

    private final String value;

    TokenStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
