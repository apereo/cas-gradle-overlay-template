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
    //private static final Logger log = Logger.getLogger(MasheryController.class);

    @Value("${mashery.service.key}")
    private String serviceKey;

    @Autowired
    private MasheryService masheryService;

    @Autowired
    private UserService userService;

    @Autowired
    private CrmService crmService;

    @RequestMapping
    public ModelAndView manageAccounts(Long userId, Long infusionsoftAccountId) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("appsGrantedAccess", getMasheryApplicationsByUserContext(infusionsoftAccountId));
        model.put("infusionsoftAccountId", infusionsoftAccountId);
        return new ModelAndView("central/manageAccounts", model);
    }

    @RequestMapping
    public ModelAndView revokeAccess(Long infusionsoftAccountId, Long masheryAppId) throws IOException {
        Set<MasheryUserApplication> masheryUserApplications = getMasheryApplicationsByUserContext(infusionsoftAccountId);
        for (MasheryUserApplication ma : masheryUserApplications) {
            if(masheryAppId == Long.parseLong(ma.getId())){
                for(String accessToken: ma.getAccess_tokens()){
                    masheryService.revokeAccessToken(serviceKey, ma.getClient_id(), accessToken);
                }
                break;
            }
        }
        return null;
    }

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
    public String viewAccessToken(Model model, String accessToken) {
        if (StringUtils.isNotBlank(accessToken)) {
            MasheryAccessToken masheryAccessToken = masheryService.fetchAccessToken(serviceKey, accessToken);

            model.addAttribute("masheryAccessToken", masheryAccessToken);
        }
        return "mashery/viewAccessToken";
    }

    private Set<MasheryUserApplication> getMasheryApplicationsByUserContext(Long infusionsoftAccountId /* CRM account id, for now*/){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserAccount> userAccountList = userService.findSortedUserAccounts(user);
        UserAccount selectedUserAccount = null;
        for (UserAccount ua : userAccountList) {
            if (ua.getId() == infusionsoftAccountId) {
                selectedUserAccount = ua;
                break;
            }
        }
        if (selectedUserAccount != null) {
            String userContext = user.getUsername() + "|" + crmService.buildCrmHostName(selectedUserAccount.getAppName());
            return masheryService.fetchUserApplications(serviceKey, /*"bradb@infusionsoft.com"*/ userContext, TokenStatus.Active);
        }
        return null;
    }
}
