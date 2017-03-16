package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A resource owner grant token provider
 */
@Component
public class OAuthResourceOwnerTokenProvider implements OAuthFilterTokenProvider {

    @Override
    public OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {
        String username = StringUtils.defaultString(request.getParameter("username")).trim();
        String password = StringUtils.defaultString(request.getParameter("password"));

        if (!OAuthGrantType.RESOURCE_OWNER_CREDENTIALS.isValueEqual(grantType)) {
            return null;
        }

        if (StringUtils.isBlank(clientSecret)) {
            throw new OAuthInvalidRequestException("oauth.exception.clientSecret");
        }
        if (StringUtils.isBlank(clientId)) {
            throw new OAuthInvalidRequestException("oauth.exception.clientId.missing");
        }
        if (StringUtils.isBlank(clientId)) {
            throw new OAuthInvalidRequestException("oauth.exception.username.missing");
        }
        if (StringUtils.isBlank(clientId)) {
            throw new OAuthInvalidRequestException("oauth.exception.password.missing");
        }

        return new OAuthResourceOwnerAuthenticationToken(username, password, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application);
    }
}
