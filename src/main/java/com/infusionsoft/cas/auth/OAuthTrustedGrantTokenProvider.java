package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import com.infusionsoft.cas.oauth.services.OAuthService;
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
    public OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret) {
        final String globalUserIdString = request.getParameter("global_user_id");

        if (!OAuthGrantType.EXTENDED_TRUSTED.isValueEqual(grantType)) {
            return null;
        }

        if (StringUtils.isBlank(clientId)) {
            throw new OAuthInvalidRequestException("oauth.exception.clientId.missing");
        }
        if (StringUtils.isBlank(clientSecret)) {
            throw new OAuthInvalidRequestException("oauth.exception.clientSecret.missing");
        }

        if (StringUtils.isBlank(globalUserIdString)) {
            throw new OAuthInvalidRequestException("oauth.exception.userId.missing");
        }
        if (!StringUtils.isNumeric(globalUserIdString)) {
            throw new OAuthInvalidRequestException("oauth.exception.userId.bad");
        }
        Long globalUserId = Long.parseLong(globalUserIdString, 10);

        return new OAuthTrustedGrantAuthenticationToken(null, null, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, globalUserId);
    }
}
