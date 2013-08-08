package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserPassword;
import com.infusionsoft.cas.services.PasswordService;
import com.infusionsoft.cas.services.UserService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CasUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    @Autowired
    UserService userService;

    @Autowired
    PasswordService passwordService;

    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken casAssertionAuthenticationToken) throws UsernameNotFoundException {
        String usernameOrId = casAssertionAuthenticationToken.getName();
        User user;
        if (NumberUtils.isDigits(usernameOrId)) {
            try {
                user = userService.loadUser(Long.parseLong(usernameOrId, 10));
            } catch (NumberFormatException e) {
                user = null;
            }
        } else {
            user = userService.loadUser(usernameOrId);
        }

        if (user != null) {
            UserPassword userPassword = passwordService.getActivePasswordForUser(user);

            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(!passwordService.isPasswordExpired(userPassword));
            user.setPassword(userPassword.getPasswordEncoded());
        } else {
            throw new UsernameNotFoundException("Unable to find a user with the ID of " + usernameOrId);
        }

        return user;
    }
}
