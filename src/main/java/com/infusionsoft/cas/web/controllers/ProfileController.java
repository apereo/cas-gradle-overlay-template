package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.exceptions.InfusionsoftValidationException;
import com.infusionsoft.cas.services.AutoLoginService;
import com.infusionsoft.cas.services.PasswordService;
import com.infusionsoft.cas.services.UserService;
import com.infusionsoft.cas.web.ValidationUtils;
import com.infusionsoft.cas.web.controllers.commands.EditProfileForm;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    public String editProfile(Model model) throws IOException {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userService.loadUser(user.getUsername());

            model.addAttribute("user", user);
            model.addAttribute("editProfileLinkSelected", "selected");

            return "profile/editProfile";
        } catch (Exception e) {
            log.error("unable to load user for current request!", e);

            return "redirect:/central/home";
        }
    }

    /**
     * Updates the user profile.
     */
    @RequestMapping
    public String updateProfile(@ModelAttribute("editProfileForm") EditProfileForm editProfileForm, Model model) throws IOException {
        User user = userService.loadUser(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        try {

            if (model.containsAttribute("error")) {
                log.info("couldn't update user account for user " + user.getId() + ": " + model.asMap().get("error"));
            } else {
                user.setFirstName(editProfileForm.getFirstName());
                user.setLastName(editProfileForm.getLastName());
                user = userService.saveUser(user);
            }
        } catch (InfusionsoftValidationException e) {
            log.error("Failed to create user account", e);
            model.addAttribute("error", e.getErrorMessageCode());
        } catch (Exception e) {
            log.error("Failed to update user account", e);
            model.addAttribute("error", "editprofile.error.exception");
        }

        model.addAttribute("user", user);
        model.addAttribute("editProfileLinkSelected", "selected");

        if (!model.containsAttribute("error")) {
            model.addAttribute("success", "Updated Profile Successfully");
        }

        return "profile/editProfile";
    }


    /**
     * Updates the user password.
     */
    @RequestMapping
    public String changePassword(Model model, String error, String success) throws IOException {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            model.addAttribute("user", user);
            model.addAttribute("changeProfileLinkSelected", "selected");
            model.addAttribute("error", ValidationUtils.sanitizeMessageCode(error));
            model.addAttribute("success", ValidationUtils.sanitizeMessageCode(success));

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

        if (StringUtils.isEmpty(password1)) {
            model.addAttribute("error", "password.error.blank");
        } else if (!password1.equals(password2)) {
            model.addAttribute("error", "password.error.passwords.dont.match");
        } else {
            User user = userService.loadUser(username);
            if (user == null || !passwordService.isPasswordCorrect(user, currentPassword)) {
                model.addAttribute("error", "editprofile.error.incorrectCurrentPassword");
            } else {
                try {
                    passwordService.setPasswordForUser(user, password1);
                } catch (InfusionsoftValidationException e) {
                    model.addAttribute("error", e.getErrorMessageCode());
                }
            }
        }

        if (model.containsAttribute("error")) {
            return "redirect:" + redirectView;
        } else {
            autoLoginService.autoLogin(username, request, response);

            model.addAttribute("success", "editprofile.success.changePassword");

            return "redirect:" + redirectView;
        }
    }

    @RequestMapping
    public ResponseEntity<String> ajaxUpdatePassword(String username, String currentPassword, String password1, String password2, HttpServletRequest request, HttpServletResponse response) {

        String error = null;

        if (StringUtils.isEmpty(password1)) {
            error = "password.error.blank";
        } else if (!password1.equals(password2)) {
            error = "password.error.passwords.dont.match";
        } else {
            User user = userService.loadUser(username);
            if (user == null || !passwordService.isPasswordCorrect(user, currentPassword)) { // TODO: why are we passing the current password in here? This is only called from AJAX so it's not even being entered by the user-- and by using it here it has to be emitted as a hidden form input!
                error = "editprofile.error.incorrectCurrentPassword";
            } else {
                try {
                    passwordService.setPasswordForUser(user, password1);
                } catch (InfusionsoftValidationException e) {
                    error = e.getErrorMessageCode();
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
