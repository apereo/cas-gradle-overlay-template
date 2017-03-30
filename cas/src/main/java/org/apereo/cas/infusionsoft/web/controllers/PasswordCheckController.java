package org.apereo.cas.infusionsoft.web.controllers;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.infusionsoft.authentication.LoginResult;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController("passwordCheckController")
@RequestMapping(value = "/password/check")
public class PasswordCheckController {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    PasswordService passwordService;

    @PostMapping
    @ResponseBody
    public boolean checkPasswordForLast4WithOldPassword(String username, String currentPassword, String password) throws Exception {
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(username, currentPassword);

        return loginResult.getLoginStatus().isSuccessful() && StringUtils.isNotBlank(password) && !passwordService.lastFourPasswordsContains(loginResult.getUser(), password);
    }

}
