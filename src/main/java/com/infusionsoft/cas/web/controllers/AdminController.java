package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.services.PasswordService;
import com.infusionsoft.cas.services.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

    private static final Logger log = Logger.getLogger(AdminController.class);

    @Autowired
    PasswordService passwordService;

    @Autowired
    UserService userService;

    @RequestMapping
    public ModelAndView userSearch(String username) {
        ModelAndView retVal = new ModelAndView("admin/userSearch");
        retVal.getModel().put("users", userService.findByUsernameWildcard(username));

        return retVal;
    }

    @RequestMapping
    public ModelAndView resetPassword(Long id) {
        ModelAndView retVal = new ModelAndView("admin/userSearch");

        User user = userService.loadUser(id);
        String recoveryCode = userService.resetPassword(user);

        retVal.getModel().put("recoveryCode", recoveryCode);

        return retVal;

    }
}
