package org.apereo.cas.infusionsoft.web.controllers;

import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.services.PasswordService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
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

    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;
    private PasswordService passwordService;
    private TicketRegistry ticketRegistry;
    private UserService userService;

    public PasswordCheckController(CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator, PasswordService passwordService, TicketRegistry ticketRegistry, UserService userService) {
        this.ticketGrantingTicketCookieGenerator = ticketGrantingTicketCookieGenerator;
        this.passwordService = passwordService;
        this.ticketRegistry = ticketRegistry;
        this.userService = userService;
    }

    @PostMapping
    @ResponseBody
    public boolean checkPasswordForLast4WithOldPassword(HttpServletRequest request, String password) throws Exception {
        boolean retVal = false;
        String tgtCookie = ticketGrantingTicketCookieGenerator.retrieveCookieValue(request);
        TicketGrantingTicket ticket = ticketRegistry.getTicket(tgtCookie, TicketGrantingTicket.class);

        if (ticket != null && !ticket.isExpired()) {
            Long userId = Long.parseLong(ticket.getAuthentication().getPrincipal().getId());
            User user = userService.loadUser(userId);
            retVal = !passwordService.lastFourPasswordsContains(user, password);
        }

        return retVal;
    }

}
