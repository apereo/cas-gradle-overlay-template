package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.Authority;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    UserService userService;

    @Autowired
    SupportController supportController;

    private List<Authority> loadAuthorities() {
        return userService.findAllAuthorities();
    }

    @RequestMapping("/admin/editUser/{userId}")
    public String editUser(Model model, @PathVariable("userId") Long userId) {
        model.addAttribute(userService.loadUser(userId));
        model.addAttribute("authorities", loadAuthorities());

        return "admin/editUser";
    }

    @RequestMapping
    public String saveUser(Model model, Long id, String username, String firstName, String lastName, String[] authorities) {
        List<Authority> allAuthorities = loadAuthorities();

        User user = userService.loadUser(id);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        user.getAuthorities().clear();

        if (authorities != null) {
            for (Authority authority : allAuthorities) {
                for (String authorityName : authorities) {
                    if (authority.getAuthority().equals(authorityName)) {
                        user.getAuthorities().add(authority);
                    }
                }
            }
        }

        userService.updateUser(user);
        model.addAttribute("success", "User  " + user.getUsername() + " saved successfully");

        return supportController.userSearch(model, null, 0);
    }
}
