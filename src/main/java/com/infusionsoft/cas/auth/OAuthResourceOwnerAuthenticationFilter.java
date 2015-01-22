package com.infusionsoft.cas.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * A Spring Security Filter that is responsible for extracting client credentials and user credentials
 * to be authenticated via CAS and Mashery.
 * <p/>
 * The filter was original copied from BasicAuthenticationFilter and modified from there.
 */
@Component
public class OAuthResourceOwnerAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    @Override
    protected OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, String scope, String application, String grantType, String clientId, String clientSecret) {
        String username = StringUtils.defaultString(request.getParameter("username")).trim();
        String password = StringUtils.defaultString(request.getParameter("password"));

        if (!grantType.equals("password") || clientId == null || clientSecret == null) {
            return null;
        }

        return new OAuthResourceOwnerAuthenticationToken(username, password, clientId, clientSecret, scope, grantType, application);
    }
}
