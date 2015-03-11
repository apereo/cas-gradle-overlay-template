package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.SecurityQuestionService;
import com.infusionsoft.cas.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SupportController {

    @Autowired
    CrmService crmService;

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    SecurityQuestionService securityQuestionService;

    @Autowired
    UserService userService;

    @Value("${infusionsoft.crm.domain}")
    private String crmDomain;

    @Value("${infusionsoft.crm.port}")
    private int crmPort;

    @RequestMapping
    public String userSearch(Model model, String searchUsername, Integer page) {
        Page<User> users = userService.findByUsernameLike(searchUsername, new PageRequest(page != null ? page : 0, 10));
        model.addAttribute("users", users);
        model.addAttribute("userList", users.getContent());
        model.addAttribute("searchUsername", searchUsername);
        model.addAttribute("crmDomain", crmDomain);
        model.addAttribute("crmPort", crmPort);

        return "support/userSearch";
    }

    @RequestMapping
    public String resetPassword(Long id, Model model) {
        User user = userService.loadUser(id);
        String recoveryCode = userService.resetPassword(user);

        model.addAttribute("success", "Recovery Code " + recoveryCode + " sent to " + user.getUsername());

        return userSearch(model, null, 0);
    }

    @RequestMapping
    public String unlockUser(Model model, Long id) {
        User user = userService.loadUser(id);
        infusionsoftAuthenticationService.unlockUser(user.getUsername());

        model.addAttribute("success", "Unlocked " + user.getUsername());

        return userSearch(model, null, 0);
    }

    @RequestMapping
    public String resetSecurityQuestion(Long id, Model model) {
        User user = userService.loadUser(id);
        securityQuestionService.deleteResponses(user);

        model.addAttribute("success", "Deleted security question responses for " + user.getUsername());

        return userSearch(model, null, 0);
    }

    @RequestMapping
    public void infusionsoftIdSearch(Model model, String query) {
        List<Map<String, String>> retVal = new ArrayList<Map<String, String>>();

        Page<UserAccount> userAccounts = userService.findUserAccountsByUsernameLikeOrAppNameLikeAndAppType(query, query, AppType.CRM, new PageRequest(0, 20));

        for(UserAccount userAccount : userAccounts.getContent()) {
            Map<String, String> userAccountMap = new HashMap<String, String>();
            userAccountMap.put("infusionsoftId", userAccount.getUser().getUsername());
            userAccountMap.put("appName", userAccount.getAppName());
            userAccountMap.put("appUrl", crmService.buildCrmUrl(userAccount.getAppName()));
            retVal.add(userAccountMap);
        }

        model.addAttribute("userAccounts", retVal);
    }
}
