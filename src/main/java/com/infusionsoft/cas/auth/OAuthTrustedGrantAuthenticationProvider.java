package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.oauth.exceptions.OAuthException;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class OAuthTrustedGrantAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    OAuthService oAuthService;

    @Autowired
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthTrustedGrantAuthenticationToken retVal = null;
        OAuthTrustedGrantAuthenticationToken oAuthTrustedGrantAuthenticationToken = (OAuthTrustedGrantAuthenticationToken) authentication;

        try {
            if (oAuthTrustedGrantAuthenticationToken != null) {

                if (oAuthService.isClientAuthorizedForTrustedGrantType(oAuthTrustedGrantAuthenticationToken.getClientId())) {
                    User user = userService.loadUser(oAuthTrustedGrantAuthenticationToken.getGlobalUserId());

                    if(user != null) {
                        retVal = new OAuthTrustedGrantAuthenticationToken(user, null, oAuthTrustedGrantAuthenticationToken.getServiceConfig(), oAuthTrustedGrantAuthenticationToken.getClientId(), oAuthTrustedGrantAuthenticationToken.getClientSecret(), oAuthTrustedGrantAuthenticationToken.getScope(), oAuthTrustedGrantAuthenticationToken.getGrantType(), oAuthTrustedGrantAuthenticationToken.getApplication(), oAuthTrustedGrantAuthenticationToken.getGlobalUserId(), user.getAuthorities());
                    } else {
                        throw new UsernameNotFoundException("Unable to find user");
                    }
                } else {
                    throw new AccessDeniedException("Client is not authorized for extended grant type");
                }
            }
        }catch (OAuthException e) {
            throw new AccessDeniedException("Unable to determine if client is authorized for extended grant type", e);
        }

        return retVal;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(OAuthTrustedGrantAuthenticationToken.class);
    }
}
