package com.infusionsoft.cas.oauth.controller;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.MasheryService;
import com.infusionsoft.cas.oauth.domain.MasheryApplication;
import com.infusionsoft.cas.oauth.domain.MasheryAuthorizationCode;
import com.infusionsoft.cas.oauth.domain.MasheryMember;
import com.infusionsoft.cas.oauth.domain.MasheryOAuthApplication;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;

/**
 * Really simple controller that provides the required Authorization Server endpoints for OAuth 2.0 Grants
 */
@Controller
public class OAuthController {

    @Autowired
    CrmService crmService;

    @Autowired
    MasheryService masheryService;

    @Autowired
    UserService userService;

    /**
     * Landing page for a user to give a 3rd party application permission to their API
     */
    @RequestMapping
    public ModelAndView authorize(String client_id, String redirect_uri, String response_type, String scope) throws Exception {
        ModelAndView modelAndView = new ModelAndView("oauth/authorize");

        //Sleep is for Mashery throttling
        MasheryOAuthApplication masheryOAuthApplication = masheryService.fetchOAuthApplication(client_id, redirect_uri, response_type);
        MasheryApplication masheryApplication = masheryService.fetchApplication(masheryOAuthApplication.getId());
        MasheryMember masheryMember = masheryService.fetchMember(masheryApplication.getUsername());

        modelAndView.addObject("masheryApplication", masheryApplication);
        modelAndView.addObject("masheryMember", masheryMember);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserAccount> accounts = userService.findSortedUserAccounts(user);
        List<String> crmAccounts = new LinkedList<String>();

        for (UserAccount userAccount : accounts) {
            if (userAccount.getAppType().equals(AppType.CRM)) {
                crmAccounts.add(crmService.buildCrmHostName(userAccount.getAppName()));
            }
        }

        modelAndView.addObject("client_id", client_id);
        modelAndView.addObject("redirect_uri", redirect_uri);
        modelAndView.addObject("requestedScope", scope);
        modelAndView.addObject("apps", crmAccounts);

        return modelAndView;
    }

    /**
     * Action to grant access to the requesting application
     */
    @RequestMapping
    public ModelAndView processAuthorization(String allow, String client_id, String redirect_uri, String requestedScope, String application) {
        if (allow != null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<UserAccount> accounts = userService.findSortedUserAccounts(user);
            List<String> crmAccounts = new LinkedList<String>();

            for (UserAccount userAccount : accounts) {
                if (userAccount.getAppType().equals(AppType.CRM)) {
                    crmAccounts.add(crmService.buildCrmHostName(userAccount.getAppName()));
                }
            }

            if (crmAccounts.contains(application)) {
                MasheryAuthorizationCode masheryAuthorizationCode = masheryService.createAuthorizationCode(client_id, requestedScope, application, redirect_uri, user.getUsername());

                if (masheryAuthorizationCode != null && masheryAuthorizationCode.getUri() != null) {
                    return new ModelAndView("redirect:" + masheryAuthorizationCode.getUri().getUri());
                }
            }
        }

        return new ModelAndView("oauth/denied");
    }
}
