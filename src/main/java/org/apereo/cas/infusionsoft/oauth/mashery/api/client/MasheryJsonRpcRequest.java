package org.apereo.cas.infusionsoft.oauth.mashery.api.client;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MasheryJsonRpcRequest {
    private String jsonrpc;
    private String method;
    private List<Object> params = new LinkedList<Object>();
    private int id;

    public MasheryJsonRpcRequest() {
        jsonrpc = "2.0";
        id = 1;
    }

    @JsonProperty("jsonrpc")
    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
