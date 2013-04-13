package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    UserService userService;

    @RequestMapping
    public ModelAndView userSearch(String username) {
        ModelAndView retVal = new ModelAndView("admin/userSearch");

        if (StringUtils.isNotEmpty(username)) {
            retVal.getModel().put("users", userService.findByUsernameWildcard(StringUtils.trim(username)));
            retVal.getModel().put("username", username);
        }

        return retVal;
    }

    @RequestMapping
    public ModelAndView resetPassword(Long id) {
        ModelAndView retVal = new ModelAndView("admin/userSearch");

        User user = userService.loadUser(id);
        String recoveryCode = userService.resetPassword(user);

        retVal.getModel().put("success", "Recovery Code Successful: " + recoveryCode);

        return retVal;

    }

    @RequestMapping
    public ModelAndView unlockUser(Long id) {
        ModelAndView retVal = new ModelAndView("admin/userSearch");

        User user = userService.loadUser(id);
        infusionsoftAuthenticationService.unlockUser(user.getUsername());

        return retVal;

    }
}
