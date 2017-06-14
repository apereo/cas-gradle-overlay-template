package org.apereo.cas.infusionsoft.web.controllers;

import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.services.AutoLoginService;
import org.apereo.cas.infusionsoft.services.PasswordService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Controller
@RestController("passwordCheckController")
@RequestMapping(value = "/password/check")
public class PasswordCheckController {

    private PasswordService passwordService;
    private UserService userService;
    private AutoLoginService autoLoginService;

    public PasswordCheckController(PasswordService passwordService, UserService userService, AutoLoginService autoLoginService) {
        this.passwordService = passwordService;
        this.userService = userService;
        this.autoLoginService = autoLoginService;
    }

    @PostMapping
    @ResponseBody
    public boolean checkPasswordForLast4WithOldPassword(HttpServletRequest request, String password) throws Exception {
        boolean retVal = false;
        TicketGrantingTicket ticket = autoLoginService.getValidTGTFromRequest(request);

        if (ticket != null) {
            Long userId = Long.parseLong(ticket.getAuthentication().getPrincipal().getId());
            User user = userService.loadUser(userId);
            retVal = !passwordService.lastFourPasswordsContains(user, password);
        }

        return retVal;
    }

}
