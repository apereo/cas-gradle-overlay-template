package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.services.OAuthService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * A Spring Security Filter that is responsible for extracting client credentials and user credentials
 * to be authenticated via CAS and Mashery.
 * <p/>
 * The filter was original copied from BasicAuthenticationFilter and modified from there.
 */
@Component
public class OAuthExtendedGrantAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    @Autowired
    protected OAuthService oAuthService;

    @Override
    protected OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, String scope, String application, String grantType, String clientId, String clientSecret) {
        String userContext = StringUtils.defaultString(request.getParameter("user_context")).trim();
        Long globalUserId = NumberUtils.createLong(request.getParameter("global_user_id"));

        if (!oAuthService.isTrustedGrantType(grantType) || clientId == null || clientSecret == null) {
            return null;
        }

        return new OAuthExtendedGrantAuthenticationToken(userContext, null, clientId, clientSecret, scope, grantType, application, globalUserId);
    }
}
