package com.infusionsoft.cas.oauth.mashery.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasheryTokenData {

    private String grant_type;
    private String scope;
    private String code;
    private String response_type = "code";
    private String refresh_token;
    private String access_token;

    public MasheryTokenData(String grant_type, String scope, String code, String response_type, String refresh_token) {
        this.grant_type = grant_type;
        this.scope = scope;
        this.code = code;
        this.response_type = response_type;
        this.refresh_token = refresh_token;
    }

    public String getGrant_type() {
        return grant_type;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getScope() {
        return scope;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getCode() {
        return code;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public void setCode(String code) {
        this.code = code;
    }

    public String getResponse_type() {
        return response_type;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
