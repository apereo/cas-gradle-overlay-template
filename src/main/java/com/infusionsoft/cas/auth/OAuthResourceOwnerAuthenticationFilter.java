package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Spring Security Filter that is responsible for extracting client credentials and user credentials
 * to be authenticated via CAS and Mashery.
 *
 * The filter was original copied from BasicAuthenticationFilter and modified from there.
 */
@Component
public class OAuthResourceOwnerAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    @Override
    protected OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {
        String username = StringUtils.defaultString(request.getParameter("username")).trim();
        String password = StringUtils.defaultString(request.getParameter("password"));

        if (!OAuthGrantType.RESOURCE_OWNER_CREDENTIALS.isValueEqual(grantType) || clientId == null || clientSecret == null) {
            return null;
        }

        return new OAuthResourceOwnerAuthenticationToken(username, password, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application);
    }
}
