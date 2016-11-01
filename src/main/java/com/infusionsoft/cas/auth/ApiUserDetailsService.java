package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User details service for the API.
 */
@Service
public class ApiUserDetailsService implements AuthenticationUserDetailsService<ApiAuthenticationToken> {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    public UserDetails loadUserDetails(ApiAuthenticationToken token) throws UsernameNotFoundException {
        final Long globalUserId = token.getGlobalUserId();
        final User user = userService.loadUser(globalUserId);
        if (user == null) {
            throw new UsernameNotFoundException(messageSource.getMessage("cas.exception.user.not.found", new Object[]{globalUserId}, null));
        }
        return user;
    }

}
