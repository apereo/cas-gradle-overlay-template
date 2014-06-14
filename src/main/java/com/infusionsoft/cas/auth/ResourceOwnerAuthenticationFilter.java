package com.infusionsoft.cas.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class ResourceOwnerAuthenticationFilter extends GenericFilterBean {

    protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @Autowired
    @Qualifier("casAuthenticationManager")
    private AuthenticationManager authenticationManager;

    private String credentialsCharset = "UTF-8";

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;

        String grantType =StringUtils.defaultString( request.getParameter("grant_type"));
        String username = StringUtils.defaultString(request.getParameter("username")).trim();
        String password = StringUtils.defaultString(request.getParameter("password"));
        String clientId = StringUtils.defaultString(request.getParameter("client_id"));
        String clientSecret = StringUtils.defaultString(request.getParameter("client_secret"));

        if (!grantType.equals("password")) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Basic ")) {
            String[] clientCredentials = extractAndDecodeHeader(header);
            clientId = clientCredentials[0];
            clientSecret = clientCredentials[1];
        }

        if (clientId == null || clientSecret == null) {
            chain.doFilter(request, response);
            return;
        }

        OAuthClientCredentialAuthenticationToken authRequest = new OAuthClientCredentialAuthenticationToken(username, password, clientId, clientSecret);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        Authentication authResult = authenticationManager.authenticate(authRequest);

        SecurityContextHolder.getContext().setAuthentication(authResult);

        chain.doFilter(request, response);
    }

    /**
     * Decodes the header into a username and password.
     *
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
        return new String[] {token.substring(0, delim), token.substring(delim + 1)};
    }
}
