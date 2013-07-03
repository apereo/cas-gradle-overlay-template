package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.Authority;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.exceptions.InfusionsoftValidationException;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Controller
public class AdminController {

    private static final Logger log = Logger.getLogger(AdminController.class);

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
        return editUser(model, userService.loadUser(userId), loadAuthorities());
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

        try {
            userService.saveUser(user);
            model.addAttribute("success", "User  " + user.getUsername() + " saved successfully");
        } catch (TransactionSystemException e) {
            log.error("Failed to update user account", e);
            Throwable cause = e.getCause();
            if (cause != null && cause.getCause() != null) {
                cause = cause.getCause();
            }
            if (cause != null) {
                model.addAttribute("error", cause.getMessage());
            } else {
                model.addAttribute("error", e.getMessage());
            }
            return editUser(model, user, allAuthorities);
        } catch (Exception e) {
            log.error("Failed to update user account", e);
            model.addAttribute("error", e.getMessage());
            return editUser(model, user, allAuthorities);
        }

        return supportController.userSearch(model, null, 0);
    }

    private String editUser(Model model, User user, List<Authority> authorities) {
        model.addAttribute("user", user);
        model.addAttribute("authorities", authorities);

        return "admin/editUser";
    }
}
