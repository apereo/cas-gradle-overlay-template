package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.infusionsoft.services.UserService;

import java.util.HashMap;
import java.util.Map;

public class InfusionsoftSocialLoginPrincipalFactory extends DefaultPrincipalFactory {

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
//        if(userService)


        return super.createPrincipal(id, attributes);
    }
}
