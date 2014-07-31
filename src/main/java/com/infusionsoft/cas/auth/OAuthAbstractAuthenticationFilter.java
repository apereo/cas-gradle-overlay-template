package com.infusionsoft.cas.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public abstract class OAuthAbstractAuthenticationFilter extends GenericFilterBean {

    protected String credentialsCharset = "UTF-8";

    protected String scope;
    protected String application;
    protected String grantType;

    protected String clientId;
    protected String clientSecret;

    protected String passedScopeUnSplit;
    protected String[] passedScope;

    protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @Autowired
    @Qualifier("casAuthenticationManager")
    protected AuthenticationManager authenticationManager;

    protected void extractRequestData(HttpServletRequest request) throws IOException {
        grantType = StringUtils.defaultString(request.getParameter("grant_type"));
        clientId = StringUtils.defaultString(request.getParameter("client_id"));
        clientSecret = StringUtils.defaultString(request.getParameter("client_secret"));
        passedScopeUnSplit = StringUtils.defaultString(request.getParameter("scope"));
        passedScope = StringUtils.split(passedScopeUnSplit, "|");

        if (passedScope.length > 1) {
            scope = passedScope[0];
            application = passedScope[1];
        } else if (passedScope.length == 1) {
            scope = "";
            application = passedScope[0];
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Basic ")) {
            String[] clientCredentials = extractAndDecodeHeader(header);
            clientId = clientCredentials[0];
            clientSecret = clientCredentials[1];
        }
    }

    /**
     * Decodes the header into a username and password.
     * <p/>
     * Copied from BasicAuthenticationFilter
     *
     * @throws org.springframework.security.authentication.BadCredentialsException if the Basic header is not present or is not valid Base64
     */
    private String[] extractAndDecodeHeader(String header) throws IOException {

        byte[] base64Token = header.substring(6).getBytes(credentialsCharset);
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, credentialsCharset);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }
}
