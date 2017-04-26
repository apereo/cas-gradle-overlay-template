package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserIdentity;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.apereo.cas.infusionsoft.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        String email = extractEmail(attributes);

        UserIdentity userIdentity = userService.findUserIdentityByExternalId(id);

        if (userIdentity == null) {
            //Attempt to find existing user with email
            User user = userService.loadUser(email);

            if (user == null) {
                //No existing user found create new user and identity
                user = new User();
                user.setFirstName(extractFirstName(attributes));
                user.setLastName(extractLastName(attributes));
                user.setUsername(email);
            }

            userIdentity = new UserIdentity();
            userIdentity.setUser(user);
            userIdentity.setExternalId(id);

            try {
                userService.saveUser(user);
                userService.saveUserIdentity(userIdentity);
            } catch (InfusionsoftValidationException e) {
                LOGGER.error("Unable to create user from social login authentication", e);
            }
        }

        attributes = userService.createAttributeMapForUser(userIdentity.getUser());

        return super.createPrincipal(userIdentity.getUser().getId().toString(), attributes);
    }

    private String extractFirstName(Map<String, Object> attributes) {
        ArrayList<String> possibleKeys = new ArrayList<>();
        possibleKeys.add("firstName");
        possibleKeys.add("first_name");

        return extractFromAttributes(attributes, possibleKeys);
    }

    private String extractLastName(Map<String, Object> attributes) {
        ArrayList<String> possibleKeys = new ArrayList<>();
        possibleKeys.add("lastName");
        possibleKeys.add("last_name");

        return extractFromAttributes(attributes, possibleKeys);
    }

    private String extractEmail(Map<String, Object> attributes) {
        ArrayList<String> possibleKeys = new ArrayList<>();
        possibleKeys.add("email");
        possibleKeys.add("emailAddress");

        return extractFromAttributes(attributes, possibleKeys);
    }

    private String extractFromAttributes(@NotNull Map<String, Object> attributes, @NotNull @Size(min = 1) List<String> possibleKeys) {
        String retVal = null;

        for (String possibleKey : possibleKeys) {
            if (attributes.containsKey(possibleKey)) {
                retVal = (String) attributes.get(possibleKey);
                break;
            }
        }

        return retVal;
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
