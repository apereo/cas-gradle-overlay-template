package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.exceptions.*;
import com.infusionsoft.cas.oauth.mashery.api.domain.*;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller that provides the required Authorization Server endpoints for OAuth 2.0 Grants
 * These endpoints call the Mashery API to verify clients, obtain authorization codes, and allows the user to accept/deny access.
 */
@Controller
public class OAuthController {

    private Logger logger = LoggerFactory.getLogger(OAuthController.class);

    @Autowired
    CrmService crmService;

    @Autowired
    OAuthService oauthService;

    @Autowired
    UserService userService;

    @Value("${cas.viewResolver.basename}")
    private String viewResolverBaseName;

    private String getViewBase() {
        return "default_views".equals(viewResolverBaseName) ? "" : viewResolverBaseName + "/";
    }

    @ExceptionHandler(OAuthException.class)
    public ModelAndView handleOAuthException(OAuthException e, HttpServletRequest request) {
        Map<String, String> model = new HashMap<String, String>();
        model.put("error", e.getErrorCode());
        model.put("state", request.getParameter("state"));

        logger.info("Unhandled OAuthException", e);

        return new ModelAndView("redirect:" + request.getParameter("redirect_uri"), model);
    }

    /**
     * Landing page for a user to give a 3rd party application permission to their API
     */
    @RequestMapping
    public String authorize(Model model, String client_id, String redirect_uri, String response_type, String scope, String state) throws Exception {

        model.addAttribute("redirect_uri", redirect_uri);
        model.addAttribute("state", state);

        if (StringUtils.isBlank(client_id) || StringUtils.isBlank(redirect_uri) || StringUtils.isBlank(response_type)) {
            throw new OAuthInvalidRequestException();
        } else if (!"code".equals(response_type)) {
            throw new OAuthUnsupportedResponseTypeException();
        } else {
            MasheryOAuthApplication masheryOAuthApplication = oauthService.fetchOAuthApplication(client_id, redirect_uri, response_type);

            MasheryApplication masheryApplication = oauthService.fetchApplication(masheryOAuthApplication.getId());
            MasheryMember masheryMember = oauthService.fetchMember(masheryApplication.getUsername());

            model.addAttribute("masheryApplication", masheryApplication);
            model.addAttribute("masheryMember", masheryMember);

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<UserAccount> accounts = userService.findSortedUserAccountsByAppType(user, AppType.CRM);

            model.addAttribute("client_id", client_id);
            model.addAttribute("redirect_uri", redirect_uri);
            model.addAttribute("requestedScope", scope);
            model.addAttribute("apps", crmService.extractAppNames(accounts));

            return "oauth/" + getViewBase() + "authorize";
        }
    }

    /**
     * Action to grant access to the requesting application
     */
    @RequestMapping
    public String processAuthorization(String allow, String client_id, String redirect_uri, String requestedScope, String application, String state) throws OAuthException {

        if (allow != null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<UserAccount> accounts = userService.findSortedUserAccountsByAppType(user, AppType.CRM);
            List<String> crmAccounts = crmService.extractAppNames(accounts);

            if (crmAccounts.contains(application)) {
                MasheryAuthorizationCode masheryAuthorizationCode = oauthService.createAuthorizationCode(client_id, requestedScope, application, redirect_uri, user.getId(), state);

                if (masheryAuthorizationCode != null && masheryAuthorizationCode.getUri() != null) {
                    return "redirect:" + masheryAuthorizationCode.getUri().getUri();
                } else {
                    throw new OAuthServerErrorException();
                }
            } else {
                logger.error("User " + SecurityContextHolder.getContext().getAuthentication().getName() + " tried to parameter tamper the application scope (" + application + ").");
                throw new OAuthAccessDeniedException();
            }
        } else {
            throw new OAuthAccessDeniedException();
        }
    }

    /**
     * Allows user to view to all apps granted access to their CRM account via oauth.
     */
    @RequestMapping
    public ModelAndView manageAccounts(Long userId, Long infusionsoftAccountId) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = userService.loadUser(userId);
        UserAccount ua = userService.findUserAccount(user, infusionsoftAccountId);
        model.put("appsGrantedAccess", oauthService.fetchUserApplicationsByUserAccount(ua));
        model.put("infusionsoftAccountId", infusionsoftAccountId);

        return new ModelAndView("oauth/manageAccounts", model);
    }

    /**
     * Allows user to revoke access to any app granted access to their CRM account via oauth.
     */
    @RequestMapping
    public ModelAndView revokeAccess(HttpServletResponse response, Long userId, Long infusionsoftAccountId, Long masheryAppId) throws IOException {
        User user = userService.loadUser(userId);
        UserAccount ua = userService.findUserAccount(user, infusionsoftAccountId);
        Set<MasheryUserApplication> masheryUserApplications = oauthService.fetchUserApplicationsByUserAccount(ua);
        for (MasheryUserApplication ma : masheryUserApplications) {
            if (masheryAppId == Long.parseLong(ma.getId())) {
                for (String accessToken : ma.getAccess_tokens()) {
                    try {
                        oauthService.revokeAccessToken(ma.getClient_id(), accessToken);
                    } catch (Exception e) {
                        logger.error("Failed to revoke app access for app= " + ma.getName(), e);
                        response.sendError(500);
                    }
                }
                break;
            }
        }

        return null;
    }

    /**
     * Admin Level User Application Searching
     *
     * @throws OAuthException
     */
    @RequestMapping
    public String userApplicationSearch(Model model, String username, String appName) throws OAuthException {
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(appName)) {
            UserAccount userAccount = userService.findUserAccountByInfusionsoftId(appName, AppType.CRM, username);
            Set<MasheryUserApplication> masheryUserApplications = oauthService.fetchUserApplicationsByUserAccount(userAccount);

            model.addAttribute("masheryUserApplications", masheryUserApplications);
            model.addAttribute("username", username);
            model.addAttribute("appName", appName);
        }

        return "oauth/userApplicationSearch";
    }

    /**
     * Admin Level Access Token Searching
     *
     * @throws OAuthException
     */
    @RequestMapping
    public String viewAccessToken(Model model, String accessToken) throws OAuthException {
        if (StringUtils.isNotBlank(accessToken)) {
            MasheryAccessToken masheryAccessToken = oauthService.fetchAccessToken(accessToken);

            model.addAttribute("masheryAccessToken", masheryAccessToken);
        }

        return "oauth/viewAccessToken";
    }

}
