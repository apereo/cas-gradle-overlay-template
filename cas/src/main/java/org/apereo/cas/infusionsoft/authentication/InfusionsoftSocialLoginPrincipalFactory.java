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
    private static  ArrayList<String> EMAIL_NAME_KEYS = new ArrayList<>();
    private static  ArrayList<String> FIRST_NAME_KEYS = new ArrayList<>();
    private static  ArrayList<String> LAST_NAME_KEYS = new ArrayList<>();


    private UserService userService;

    public InfusionsoftSocialLoginPrincipalFactory(UserService userService) {
        this.userService = userService;

        EMAIL_NAME_KEYS.add("email");
        EMAIL_NAME_KEYS.add("emailAddress");

        FIRST_NAME_KEYS.add("firstName");
        FIRST_NAME_KEYS.add("first_name");

        LAST_NAME_KEYS.add("lastName");
        LAST_NAME_KEYS.add("last_name");

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
        return extractFromAttributes(attributes, FIRST_NAME_KEYS);
    }

    private String extractLastName(Map<String, Object> attributes) {
        return extractFromAttributes(attributes, LAST_NAME_KEYS);
    }

    private String extractEmail(Map<String, Object> attributes) {
        return extractFromAttributes(attributes, EMAIL_NAME_KEYS);
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
