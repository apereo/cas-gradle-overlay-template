package com.infusionsoft.cas.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A Spring Security Filter that is responsible for extracting client credentials and user credentials
 * to be authenticated via CAS and Mashery.
 * <p/>
 * The filter was original copied from BasicAuthenticationFilter and modified from there.
 */
@Component
public class OAuthResourceOwnerAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    protected String username;
    protected String password;

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;

        extractRequestData(request);
        username = StringUtils.defaultString(request.getParameter("username")).trim();
        password = StringUtils.defaultString(request.getParameter("password"));

        String username = StringUtils.defaultString(request.getParameter("username")).trim();
        String password = StringUtils.defaultString(request.getParameter("password"));

        if (!grantType.equals("password") || clientId == null || clientSecret == null) {
            chain.doFilter(request, response);
            return;
        }

        OAuthResourceOwnerAuthenticationToken authRequest = new OAuthResourceOwnerAuthenticationToken(username, password, clientId, clientSecret, scope, grantType, application);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        Authentication authResult = authenticationManager.authenticate(authRequest);

        SecurityContextHolder.getContext().setAuthentication(authResult);

        chain.doFilter(request, response);
    }
}
