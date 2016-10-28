package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.api.domain.APIErrorDTO;
import com.infusionsoft.cas.api.domain.UserResource;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.services.SecurityService;
import com.infusionsoft.cas.services.UserService;
import com.infusionsoft.cas.support.AppHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;

@Controller
@RequestMapping("/api/user")
public class ApiUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppHelper appHelper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SecurityService securityService;

    /**
     * A simple REST endpoint for getting user information. Right now the only user supported is the value "current"
     * which returns the current signed-in user.
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity get(@PathVariable("userId") String id, Locale locale) {
        try {
            User currentUser = securityService.getCurrentUser();
            User user = null;
            if (StringUtils.equals("current", id)) {
                user = currentUser;
            } else if (NumberUtils.isDigits(id)) {
                // Only admins are allowed to see all users
                if (!securityService.isUserInRole(currentUser, "ROLE_CAS_ADMIN")) {
                    return new ResponseEntity<APIErrorDTO>(new APIErrorDTO("cas.exception.forbidden", messageSource, new Object[0], locale), HttpStatus.FORBIDDEN);
                }

                long globalUserId;
                try {
                    globalUserId = Long.parseLong(id);
                } catch (NumberFormatException e) {
                    return new ResponseEntity<APIErrorDTO>(new APIErrorDTO("cas.exception.user.get.invalid", messageSource, new Object[0], locale), HttpStatus.FORBIDDEN);
                }
                user = userService.loadUser(globalUserId);
            } else {
                return new ResponseEntity<APIErrorDTO>(new APIErrorDTO("cas.exception.user.get.invalid", messageSource, new Object[0], locale), HttpStatus.FORBIDDEN);
            }
            if (user == null) {
                return new ResponseEntity<APIErrorDTO>(new APIErrorDTO("cas.exception.user.get.not.found", messageSource, new Object[0], locale), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<UserResource>(new UserResource(user, userService.findActiveUserAccounts(user), appHelper), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<APIErrorDTO>(new APIErrorDTO("cas.exception.general", messageSource, new Object[]{e.getMessage()}, locale), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
