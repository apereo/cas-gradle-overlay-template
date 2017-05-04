package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.domain.OAuthServiceConfig;
import org.apereo.cas.infusionsoft.oauth.dto.OAuthGrantType;
import org.apereo.cas.infusionsoft.oauth.exceptions.OAuthInvalidRequestException;
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
    public OAuthResourceOwnerAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {
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
        if (StringUtils.isBlank(username)) {
            throw new OAuthInvalidRequestException("oauth.exception.username.missing");
        }
        if (StringUtils.isBlank(password)) {
            throw new OAuthInvalidRequestException("oauth.exception.password.missing");
        }

        return new OAuthResourceOwnerAuthenticationToken(username, password, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application);
    }
}
