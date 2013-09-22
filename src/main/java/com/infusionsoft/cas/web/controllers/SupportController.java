package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class SupportController {
    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    UserService userService;

    @Value("${infusionsoft.crm.domain}")
    private String crmDomain;

    @RequestMapping
    public String userSearch(Model model, String searchUsername, Integer page) {
        Page<User> users = userService.findByUsernameLike(searchUsername, new PageRequest(page != null ? page : 0, 10));
        model.addAttribute("users", users);
        model.addAttribute("userList", users.getContent());
        model.addAttribute("searchUsername", searchUsername);
        model.addAttribute("crmDomain", crmDomain);

        return "support/userSearch";
    }

    @RequestMapping
    public String resetPassword(Long id, Model model) {
        User user = userService.loadUser(id);
        String recoveryCode = userService.resetPassword(user);

        model.addAttribute("success", "Recovery Code " + recoveryCode + " sent to " + user.getUsername());

        return userSearch(model, null, 0);
    }

    @RequestMapping
    public String unlockUser(Model model, Long id) {
        User user = userService.loadUser(id);
        infusionsoftAuthenticationService.unlockUser(user.getUsername());

        model.addAttribute("success", "Unlocked " + user.getUsername());

        return userSearch(model, null, 0);
    }
}
