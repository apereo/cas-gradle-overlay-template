package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import com.infusionsoft.cas.oauth.services.OAuthService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Spring Security Filter that is responsible for extracting client credentials and user credentials
 * to be authenticated via CAS and Mashery.
 * <p/>
 * The filter was original copied from BasicAuthenticationFilter and modified from there.
 */
@Component
public class OAuthTrustedGrantAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    @Autowired
    protected OAuthService oAuthService;

    @Override
    protected OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {
        String userContext = StringUtils.defaultString(request.getParameter("user_context")).trim();
        Long globalUserId = NumberUtils.createLong(request.getParameter("global_user_id"));

        if (!OAuthGrantType.EXTENDED_TRUSTED.isValueEqual(grantType) || clientId == null || clientSecret == null) {
            return null;
        }

        return new OAuthTrustedGrantAuthenticationToken(userContext, null, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, globalUserId);
    }
}
