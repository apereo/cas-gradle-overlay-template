package com.infusionsoft.cas.oauth.dto;

/**
 * Support OAuth 2.0 Grant Types
 */
public enum OAuthGrantType {

    AUTHORIZATION_CODE("code"),
    CLIENT_CREDENTIALS("client_credentials"),
    RESOURCE_OWNER_CREDENTIALS("password"),
    REFRESH("refresh_token"),
    EXTENDED_TRUSTED("urn:infusionsoft:params:oauth:grant-type:trusted"),
    EXTENDED_TICKET_GRANTING_TICKET("urn:infusionsoft:params:oauth:grant-type:ticket-granting-ticket");

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

    public boolean isValueEqual(String value) {
        return this.value.equals(value);
    }

}
