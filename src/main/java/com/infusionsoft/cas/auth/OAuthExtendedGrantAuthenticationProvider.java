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
import org.springframework.stereotype.Component;

@Component
public class OAuthExtendedGrantAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    OAuthService oAuthService;

    @Autowired
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuthExtendedGrantAuthenticationToken retVal = null;
        OAuthExtendedGrantAuthenticationToken oAuthExtendedGrantAuthenticationToken = (OAuthExtendedGrantAuthenticationToken) authentication;

        try {
            if (oAuthExtendedGrantAuthenticationToken != null) {

                if (oAuthService.isClientAuthorizedForExtendedGrantType(oAuthExtendedGrantAuthenticationToken.getClientId())) {
                    User user = userService.loadUser(oAuthExtendedGrantAuthenticationToken.getGlobalUserId());
                    retVal = new OAuthExtendedGrantAuthenticationToken(user, null, oAuthExtendedGrantAuthenticationToken.getClientId(), oAuthExtendedGrantAuthenticationToken.getClientSecret(), oAuthExtendedGrantAuthenticationToken.getScope(), oAuthExtendedGrantAuthenticationToken.getGrantType(), oAuthExtendedGrantAuthenticationToken.getApplication(), oAuthExtendedGrantAuthenticationToken.getGlobalUserId(), user.getAuthorities());
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
        return authentication.isAssignableFrom(OAuthExtendedGrantAuthenticationToken.class);
    }
}
