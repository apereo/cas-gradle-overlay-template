package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class OAuthRefreshAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthRefreshAuthenticationToken token = (OAuthRefreshAuthenticationToken) authentication;
        String clientId = token.getClientId();

        if (token.getServiceConfig() == null) {
            throw new OAuthInvalidRequestException("oauth.exception.service.key.not.found");
        }

        String refreshToken = token.getRefreshToken();
        if (StringUtils.isNotBlank(refreshToken)) {
            return new OAuthRefreshAuthenticationToken(null, null, token.getServiceConfig(), clientId, token.getClientSecret(), token.getGrantType(), refreshToken);
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(OAuthRefreshAuthenticationToken.class);
    }
}
