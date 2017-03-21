package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class OAuthRefreshAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthRefreshAuthenticationToken token = (OAuthRefreshAuthenticationToken) authentication;
        String clientId = token.getClientId();

        if (token.getServiceConfig() == null) {
            throw new OAuthInvalidRequestException("oauth.exception.service.missing");
        }

        String refreshToken = token.getRefreshToken();
        if (StringUtils.isBlank(refreshToken)) {
            throw new OAuthInvalidRequestException("oauth.exception.refreshToken.missing");
        }
        return new OAuthRefreshAuthenticationToken(null, null, token.getServiceConfig(), clientId, token.getClientSecret(), token.getGrantType(), refreshToken, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuthRefreshAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
