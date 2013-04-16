package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.services.AutoLoginService;
import com.infusionsoft.cas.services.PasswordService;
import com.infusionsoft.cas.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProfileController {

    private static final Logger log = Logger.getLogger(ProfileController.class);

    @Autowired
    CentralController centralController;

    @Autowired
    PasswordService passwordService;

    @Autowired
    UserService userService;

    @Autowired
    AutoLoginService autoLoginService;

    /**
     * Brings up the form to edit the user profile.
     */
    @RequestMapping
    public ModelAndView editProfile() throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<String, Object>();
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userService.loadUser(user.getUsername());

            model.put("user", user);
            model.put("editProfileLinkSelected", "selected");

            return new ModelAndView("profile/editProfile", model);
        } catch (Exception e) {
            log.error("unable to load user for current request!", e);

            return new ModelAndView("redirect:/central/home");
        }
    }

    /**
     * Updates the user profile.
     */
    @RequestMapping
    public ModelAndView updateProfile(String firstName, String lastName) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();

        User user = userService.loadUser(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        try {

            if (model.containsKey("error")) {
                log.info("couldn't update user account for user " + user.getId() + ": " + model.get("error"));
            } else {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                userService.updateUser(user);
            }
        } catch (Exception e) {
            log.error("failed to update user account", e);

            model.put("error", "editprofile.error.exception");
        }

        model.put("user", user);

        if (model.containsKey("error")) {
            return new ModelAndView("profile/editProfile", model);
        } else {
            return new ModelAndView("redirect:/central/home");
        }
    }


    /**
     * Updates the user password.
     */
    @RequestMapping
    public ModelAndView changePassword() throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<String, Object>();
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            model.put("user", user);
            model.put("changeProfileLinkSelected", "selected");

            return new ModelAndView("profile/changePassword", model);
        } catch (Exception e) {
            log.error("unable to load user for current request!", e);

            return new ModelAndView("redirect:/central/home");
        }
    }

    @RequestMapping
    public String updatePassword(Model model, String username, String currentPassword, String password1, String password2, @RequestHeader String referer, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(password1) || StringUtils.isEmpty(password2)) {
            model.addAttribute("error", "registration.error.invalidPassword");
        } else if (!password1.equals(password2)) {
            model.addAttribute("error", "registration.error.passwordsNoMatch");
        } else {
            if (!passwordService.isPasswordValid(username, currentPassword)) {
                model.addAttribute("error", "editprofile.error.incorrectCurrentPassword");
            } else if (StringUtils.isNotEmpty(password1) || StringUtils.isNotEmpty(password2)) {
                User user = userService.loadUser(username);
                user.setPassword(password1);

                String passwordError = passwordService.validatePassword(user);

                if (passwordError != null) {
                    model.addAttribute("error", passwordError);
                } else {
                    passwordService.setPasswordForUser(user);
                }
            }
        }


        if (model.containsAttribute("error")) {
            return referer;
        } else {
            autoLoginService.autoLogin(username, request, response);

            if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
                return centralController.home(model);
            } else {
                return "redirect:" + referer;
            }
        }
    }
}
