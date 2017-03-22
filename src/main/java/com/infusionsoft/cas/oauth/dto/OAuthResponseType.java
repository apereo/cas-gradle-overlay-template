package com.infusionsoft.cas.oauth.dto;

/**
 * Support OAuth 2.0 Response Types
 */
public enum OAuthResponseType {

    CODE("code"),
    TOKEN("token");

    private final String value;

    OAuthResponseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OAuthResponseType fromValue(String value) {
        for (OAuthResponseType oAuthGrantType : values()) {
            if (oAuthGrantType.getValue().equals(value)) {
                return oAuthGrantType;
            }
        }

        return null;
    }

    public boolean isValueEqual(String value) {
        return this.value.equals(value);
    }

}
