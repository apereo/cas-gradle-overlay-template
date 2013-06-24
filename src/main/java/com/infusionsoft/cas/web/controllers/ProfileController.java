package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.services.AutoLoginService;
import com.infusionsoft.cas.services.PasswordService;
import com.infusionsoft.cas.services.UserService;
import com.infusionsoft.cas.web.ValidationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    MessageSource messageSource;

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
                user.setFirstName(ValidationUtils.sanitizePersonName(firstName));
                user.setLastName(ValidationUtils.sanitizePersonName(lastName));
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
    public String changePassword(Model model, String error) throws IOException {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            model.addAttribute("user", user);
            model.addAttribute("changeProfileLinkSelected", "selected");

            model.addAttribute("error", ValidationUtils.sanitizeMessageCode(error));

            return "profile/changePassword";
        } catch (Exception e) {
            log.error("unable to load user for current request!", e);

            return "redirect:/central/home";
        }
    }

    @RequestMapping
    public String updatePassword(Model model, String username, String currentPassword, String password1, String password2, String redirectFrom, HttpServletRequest request, HttpServletResponse response) {

        String redirectView;
        if ("expirePassword".equals(redirectFrom)) {
            redirectView = "/login";
        } else {
            redirectView = "/app/profile/changePassword";
        }

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
                    model.addAttribute("success", "editprofile.success.changePassword");
                }
            }
        }

        if (model.containsAttribute("error")) {
            return "redirect:" + redirectView;
        } else {
            autoLoginService.autoLogin(username, request, response);

            if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
                return centralController.home(model, null);
            } else {
                return "redirect:" + redirectView;
            }
        }
    }

    @RequestMapping
    public ResponseEntity<String> ajaxUpdatePassword(String username, String currentPassword, String password1, String password2, HttpServletRequest request, HttpServletResponse response) {

        String error = null;

        if (StringUtils.isEmpty(password1) || StringUtils.isEmpty(password2)) {
            error = "registration.error.invalidPassword";
        } else if (!password1.equals(password2)) {
            error = "registration.error.passwordsNoMatch";
        } else {
            if (!passwordService.isPasswordValid(username, currentPassword)) {
                error = "editprofile.error.incorrectCurrentPassword";
            } else if (StringUtils.isNotEmpty(password1) || StringUtils.isNotEmpty(password2)) {
                User user = userService.loadUser(username);
                user.setPassword(password1);

                String passwordError = passwordService.validatePassword(user);

                if (passwordError != null) {
                    error = passwordError;
                } else {
                    passwordService.setPasswordForUser(user);
                }
            }
        }

        if (StringUtils.isNotEmpty(error)) {

            return new ResponseEntity<String>("{\"errorMessage\": \"" + messageSource.getMessage(error, null, request.getLocale()) + "\"}", HttpStatus.UNAUTHORIZED);
        } else {
            autoLoginService.autoLogin(username, request, response);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
    }
}
