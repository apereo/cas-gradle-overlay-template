package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import com.infusionsoft.cas.oauth.exceptions.OAuthUnauthorizedClientException;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class OAuthTrustedGrantAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthTrustedGrantAuthenticationToken token = (OAuthTrustedGrantAuthenticationToken) authentication;

        if (oAuthService.isClientAuthorizedForTrustedGrantType(token.getClientId())) {
            User user = userService.loadUser(token.getGlobalUserId());

            if (user != null) {
                return new OAuthTrustedGrantAuthenticationToken(user, token.getServiceConfig(), token.getClientId(), token.getClientSecret(), token.getScope(), token.getGrantType(), token.getApplication(), token.getGlobalUserId(), user.getAuthorities());
            } else {
                throw new OAuthInvalidRequestException("oauth.exception.user.not.found");
            }
        } else {
            throw new OAuthUnauthorizedClientException("oauth.exception.client.not.trusted");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuthTrustedGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
