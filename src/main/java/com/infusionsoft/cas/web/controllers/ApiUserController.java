package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.api.domain.APIErrorDTO;
import com.infusionsoft.cas.api.domain.UserResource;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.services.SecurityService;
import com.infusionsoft.cas.services.UserService;
import com.infusionsoft.cas.support.AppHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
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
     *
     * @param id     id
     * @param locale locale
     * @return ResponseEntity
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity get(@PathVariable("userId") String id, Locale locale) {
        try {
            if (StringUtils.equals("current", id)) {
                final User user = securityService.getCurrentUser();
                final List<UserAccount> activeUserAccounts = userService.findActiveUserAccounts(user);
                return new ResponseEntity<UserResource>(new UserResource(user, activeUserAccounts, appHelper), HttpStatus.OK);
            } else {
                return new ResponseEntity<APIErrorDTO>(new APIErrorDTO("cas.exception.user.get.invalid", messageSource, new Object[0], locale), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<APIErrorDTO>(new APIErrorDTO("cas.exception.general", messageSource, new Object[]{e.getMessage()}, locale), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
