package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.SimplePrincipal;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserIdentity;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.infusionsoft.services.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class InfusionsoftSocialLoginPrincipalFactory extends DefaultPrincipalFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfusionsoftSocialLoginPrincipalFactory.class);

    private UserService userService;

    public InfusionsoftSocialLoginPrincipalFactory(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Principal createPrincipal(String id) {
        return createPrincipal(id, new HashMap<>());
    }

    @Override
    public Principal createPrincipal(String id, Map<String, Object> attributes) {
        User user = userService.findUserByExternalId(id);

        if (user == null) {
            user = new User();
            user.setFirstName((String) attributes.get("first_name"));
            user.setLastName((String) attributes.get("last_name"));
            user.setUsername((String) attributes.get("email"));

            UserIdentity userIdentity = new UserIdentity();
            userIdentity.setUser(user);
            userIdentity.setExternalId(id);

            try {
                user = userService.saveUser(user);
                userService.saveUserIdentity(userIdentity);
                attributes = userService.createAttributeMapForUser(user);
            } catch (InfusionsoftValidationException e) {
                LOGGER.error("Unable to create user from social login authentication", e);
            }
        }

        return super.createPrincipal(user.getId().toString(), attributes);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
