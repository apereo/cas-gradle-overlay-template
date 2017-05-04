package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.domain.OAuthServiceConfig;
import org.apereo.cas.infusionsoft.oauth.dto.OAuthGrantType;
import org.apereo.cas.infusionsoft.oauth.exceptions.OAuthInvalidRequestException;
import org.apereo.cas.infusionsoft.oauth.services.OAuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A trusted grant token provider
 */
@Component
public class OAuthTrustedGrantTokenProvider implements OAuthFilterTokenProvider {

    @Autowired
    protected OAuthService oAuthService;

    @Override
    public OAuthTrustedGrantAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {
        if (!OAuthGrantType.EXTENDED_TRUSTED.isValueEqual(grantType)) {
            return null;
        }

        if (StringUtils.isBlank(clientId)) {
            throw new OAuthInvalidRequestException("oauth.exception.clientId.missing");
        }
        if (StringUtils.isBlank(clientSecret)) {
            throw new OAuthInvalidRequestException("oauth.exception.clientSecret.missing");
        }

        final String globalUserIdString = request.getParameter("global_user_id");
        if (StringUtils.isBlank(globalUserIdString)) {
            throw new OAuthInvalidRequestException("oauth.exception.userId.missing");
        }
        if (!StringUtils.isNumeric(globalUserIdString)) {
            throw new OAuthInvalidRequestException("oauth.exception.userId.bad");
        }
        Long globalUserId = Long.parseLong(globalUserIdString, 10);

        return new OAuthTrustedGrantAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, globalUserId);
    }
}
