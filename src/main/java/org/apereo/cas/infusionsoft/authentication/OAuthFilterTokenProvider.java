package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.domain.OAuthServiceConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface OAuthFilterTokenProvider {
    OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret);
}
