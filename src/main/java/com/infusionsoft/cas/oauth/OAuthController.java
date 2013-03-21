package com.infusionsoft.cas.oauth;

import com.infusionsoft.cas.oauth.domain.MasheryApplication;
import com.infusionsoft.cas.oauth.domain.MasheryAuthorizationCode;
import com.infusionsoft.cas.oauth.domain.MasheryMember;
import com.infusionsoft.cas.oauth.domain.MasheryOAuthApplication;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.InfusionsoftDataService;
import com.infusionsoft.cas.types.AppType;
import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

/**
 * Really simple controller that provides the required Authorization Server endpoints for OAuth 2.0 Grants
 */
public class OAuthController extends MultiActionController {

    private CrmService crmService;
    private MasheryService masheryService;
    private InfusionsoftDataService infusionsoftDataService;
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    public void setCrmService(CrmService crmService) {
        this.crmService = crmService;
    }

    public void setMasheryService(MasheryService masheryService) {
        this.masheryService = masheryService;
    }

    public void setInfusionsoftDataService(InfusionsoftDataService infusionsoftDataService) {
        this.infusionsoftDataService = infusionsoftDataService;
    }

    public void setInfusionsoftAuthenticationService(InfusionsoftAuthenticationService infusionsoftAuthenticationService) {
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
    }

    /**
     * Landing page for a user to give a 3rd party application permission to their API
     */
    @RequestMapping
    public ModelAndView authorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String clientId = request.getParameter("client_id");
        String redirectUri = request.getParameter("redirect_uri");
        String responseType = request.getParameter("response_type");

        ModelAndView modelAndView = new ModelAndView("infusionsoft/ui/oauth/authorize");

        //Sleep is for Mashery throttling
        MasheryOAuthApplication masheryOAuthApplication = masheryService.fetchOAuthApplication(clientId, redirectUri, responseType);
        MasheryApplication masheryApplication = masheryService.fetchApplication(masheryOAuthApplication.getId());
        MasheryMember masheryMember = masheryService.fetchMember(masheryApplication.getUsername());

        modelAndView.addObject("masheryApplication", masheryApplication);
        modelAndView.addObject("masheryMember", masheryMember);

        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        List<UserAccount> accounts = infusionsoftDataService.findSortedUserAccounts(user);
        List<String> crmAccounts = new LinkedList<String>();

        for (UserAccount userAccount : accounts) {
            if (userAccount.getAppType().equals(AppType.CRM)) {
                crmAccounts.add(crmService.buildCrmHostName(userAccount.getAppName()));
            }
        }

        modelAndView.addObject("client_id", clientId);
        modelAndView.addObject("redirect_uri", redirectUri);
        modelAndView.addObject("apps", crmAccounts);

        return modelAndView;
    }

    /**
     * Action to grant access to the requesting application
     */
    @RequestMapping
    public ModelAndView processAuthorization(HttpServletRequest request, HttpServletResponse response) {
        if (request.getParameter("allow") != null) {
            String clientId = request.getParameter("client_id");
            String redirectUri = request.getParameter("redirect_uri");
            String scope = request.getParameter("scope");
            User user = infusionsoftAuthenticationService.getCurrentUser(request);
            List<UserAccount> accounts = infusionsoftDataService.findSortedUserAccounts(user);
            List<String> crmAccounts = new LinkedList<String>();

            for (UserAccount userAccount : accounts) {
                if (userAccount.getAppType().equals(AppType.CRM)) {
                    crmAccounts.add(crmService.buildCrmHostName(userAccount.getAppName()));
                }
            }

            if (crmAccounts.contains(scope)) {
                MasheryAuthorizationCode masheryAuthorizationCode = masheryService.createAuthorizationCode(clientId, scope, redirectUri, user.getUsername());

                if (masheryAuthorizationCode != null && masheryAuthorizationCode.getUri() != null) {
                    return new ModelAndView("redirect:" + masheryAuthorizationCode.getUri().getUri());
                }
            }
        }

        return new ModelAndView("infusionsoft/ui/oauth/denied");
    }
}
