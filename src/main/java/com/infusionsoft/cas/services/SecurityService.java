package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.Authority;
import com.infusionsoft.cas.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SecurityService {

    public User getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        final Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    public boolean isUserInRole(User user, String role) {
        if (user == null || StringUtils.isBlank(role)) {
            return false;
        }

        final Set<Authority> authorities = user.getAuthorities();
        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority grantedAuthority : authorities) {
            if (role.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }

        return false;
    }

    public void syncCurrentUser(User user) {
        User currentUser = getCurrentUser();
        currentUser.setUsername(user.getUsername());
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
    }

}
