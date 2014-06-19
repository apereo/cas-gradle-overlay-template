package com.infusionsoft.cas.oauth.dto;

/**
 * Support OAuth 2.0 Grant Types
 */
public enum OAuthGrantType {

    AUTHORIZATION_CODE("code"),
    RESOURCE_OWNER_CREDENTIALS("password");

    private final String value;

    OAuthGrantType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OAuthGrantType fromValue(String value) {
        for(OAuthGrantType oAuthGrantType : values()) {
            if(oAuthGrantType.getValue().equals(value)) {
                return oAuthGrantType;
            }
        }

        return null;
    }
}
