package org.apereo.cas.infusionsoft.authentication;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * An authentication provider that validates the credentials belong to an approved caller.
 * It also invokes our custom user details service to resolve to a local user, or the anonymous
 * user if no Global User ID was sent.
 */
@Component
public class ApiAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AuthenticationUserDetailsService<ApiAuthenticationToken> apiUserDetailsService;

    @Value("${infusionsoft.cas.apikey}")
    private String requiredApiKey;

    private Logger log = LoggerFactory.getLogger(ApiAuthenticationProvider.class);

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            ApiAuthenticationToken apiAuthenticationToken = (ApiAuthenticationToken) authentication;

            if (StringUtils.isBlank(apiAuthenticationToken.getApiKey())) {
                throw new BadCredentialsException("API Key not provided");
            }

            if (!requiredApiKey.equals(apiAuthenticationToken.getApiKey())) {
                throw new BadCredentialsException("Invalid API key");
            }

            if (apiAuthenticationToken.getGlobalUserId() == null) {
                throw new BadCredentialsException("Missing user ID");
            }

            UserDetails details = apiUserDetailsService.loadUserDetails(apiAuthenticationToken);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, apiAuthenticationToken.getApiKey(), details.getAuthorities());
            auth.setDetails(details);

            return auth;
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception during API authentication", e);
            throw new AuthenticationServiceException("Exception during API authentication", e);
        }
    }

    public boolean supports(Class<?> c) {
        return c.equals(ApiAuthenticationToken.class);
    }

}
