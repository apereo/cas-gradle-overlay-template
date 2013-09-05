package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.oauth.MasheryService;
import com.infusionsoft.cas.oauth.TokenStatus;
import com.infusionsoft.cas.oauth.domain.MasheryAccessToken;
import com.infusionsoft.cas.oauth.domain.MasheryUserApplication;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Controller
public class MasheryController {
    private static final Logger log = Logger.getLogger(MasheryController.class);

    @Value("${mashery.service.key}")
    private String serviceKey;

    @Autowired
    private MasheryService masheryService;

    @RequestMapping
      public String userApplicationSearch(Model model, String userContext) {
        if (StringUtils.isNotBlank(userContext)) {
            Set<MasheryUserApplication> masheryUserApplications = masheryService.fetchUserApplications(serviceKey, userContext, TokenStatus.Active);

            model.addAttribute("masheryUserApplications", masheryUserApplications);
            model.addAttribute("userContext", userContext);
        }

        return "mashery/userApplicationSearch";
    }

    @RequestMapping
    public String viewAccessToken(Model model,  String accessToken) {
        if (StringUtils.isNotBlank(accessToken)) {
            MasheryAccessToken masheryAccessToken = masheryService.fetchAccessToken(serviceKey, accessToken);

            model.addAttribute("masheryAccessToken", masheryAccessToken);
        }

        return "mashery/viewAccessToken";
    }
}
