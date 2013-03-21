package com.infusionsoft.cas.oauth;

public enum TokenStatus {
    Active("active"),
    Inactive("inactive"),
    All("all");

    private final String value;

    TokenStatus(String value) {
        this.value = value;
    }
}
