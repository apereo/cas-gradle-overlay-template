package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.MasheryService;
import com.infusionsoft.cas.oauth.TokenStatus;
import com.infusionsoft.cas.oauth.domain.MasheryAccessToken;
import com.infusionsoft.cas.oauth.domain.MasheryOAuthApplication;
import com.infusionsoft.cas.oauth.domain.MasheryUserApplication;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import com.infusionsoft.cas.web.ValidationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class MasheryController {
    private static final Logger log = Logger.getLogger(MasheryController.class);

    @Value("${mashery.service.key}")
    private String serviceKey;

    @Autowired
    private MasheryService masheryService;

    @Autowired
    private UserService userService;

    @Autowired
    private CrmService crmService;

    @RequestMapping
    public String userApplicationSearch(Model model, String userContext) {
        if (StringUtils.isNotBlank(userContext)) {
            Set<MasheryUserApplication> masheryUserApplications = masheryService.fetchUserApplicationsByUserContext(serviceKey, userContext, TokenStatus.Active);

            model.addAttribute("masheryUserApplications", masheryUserApplications);
            model.addAttribute("userContext", userContext);
        }
        return "mashery/userApplicationSearch";
    }

    @RequestMapping
    public String viewAccessToken(Model model, String accessToken) {
        if (StringUtils.isNotBlank(accessToken)) {
            MasheryAccessToken masheryAccessToken = masheryService.fetchAccessToken(serviceKey, accessToken);

            model.addAttribute("masheryAccessToken", masheryAccessToken);
        }
        return "mashery/viewAccessToken";
    }
}
