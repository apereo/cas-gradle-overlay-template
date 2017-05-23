package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.CipherExecutor;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.configuration.model.support.pm.PasswordManagementProperties;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.apereo.cas.pm.BasePasswordManagementService;
import org.apereo.cas.pm.PasswordChangeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InfusionsoftPasswordManagementService extends BasePasswordManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfusionsoftPasswordManagementService.class);

    private PasswordService passwordService;
    private UserService userService;

    public InfusionsoftPasswordManagementService(final CipherExecutor<Serializable, String> cipherExecutor,
                                                 final String issuer,
                                                 final PasswordManagementProperties passwordManagementProperties,
                                                 final PasswordService passwordService,
                                                 final UserService userService) {
        super(cipherExecutor, issuer, passwordManagementProperties);
        this.passwordService = passwordService;
        this.userService = userService;
    }

    @Override
    public boolean change(Credential c, PasswordChangeBean bean) {
        boolean retVal = false;

        UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) c;
        User user = userService.loadUser(usernamePasswordCredential.getUsername());

        try {
            passwordService.setPasswordForUser(user, bean.getPassword());
            retVal = true;
        } catch (InfusionsoftValidationException e) {
            LOGGER.error("Unable to validate users password", e);
        }

        return retVal;
    }

    @Override
    public String findEmail(String username) {
        return username;
    }

    @Override
    public Map<String, String> getSecurityQuestions(String username) {
        return new HashMap<>();
    }

}
