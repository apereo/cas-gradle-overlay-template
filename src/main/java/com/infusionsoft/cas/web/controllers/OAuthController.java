package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.auth.OAuthAuthenticationToken;
import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.dto.OAuthAccessToken;
import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import com.infusionsoft.cas.oauth.dto.OAuthUserApplication;
import com.infusionsoft.cas.oauth.exceptions.*;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Controller that provides the required Authorization Server endpoints for OAuth 2.0 Grants
 * These endpoints call the Mashery API to verify clients, obtain authorization codes, and allows the user to accept/deny access.
 */
@Controller
public class OAuthController {

    private Logger logger = LoggerFactory.getLogger(OAuthController.class);

    private final static String ERROR_PARAMETER = "error";
    private final static String ERROR__DESCRIPTION_PARAMETER = "error_description";
    private final static String ERROR__URI_PARAMETER = "error_uri";
    private final static String RESPONSE_TYPE_PARAMETER = "response_type";
    private final static String REDIRECT_URI_PARAMETER = "redirect_uri";
    private final static String STATE_PARAMETER = "state";

    @Autowired
    CrmService crmService;

    @Autowired
    OAuthService oauthService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    UserService userService;

    @Value("${cas.viewResolver.basename}")
    private String viewResolverBaseName;

    private String getViewBase() {
        return "default_views".equals(viewResolverBaseName) ? "" : viewResolverBaseName + "/";
    }

    @ExceptionHandler(OAuthException.class)
    public ModelAndView handleOAuthException(OAuthException e, HttpServletRequest request, HttpServletResponse response, Locale locale) {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, String> model = new HashMap<String, String>();

        logger.info("Unhandled OAuthException", e.getMessage());

        model.put(ERROR_PARAMETER, e.getErrorCode());

        if (StringUtils.isNotBlank(e.getErrorDescription())) {
            model.put(ERROR__DESCRIPTION_PARAMETER, messageSource.getMessage(e.getErrorDescription(), null, locale));
        }

        if (StringUtils.isNotBlank(e.getErrorUri())) {
            model.put(ERROR__URI_PARAMETER, e.getErrorUri());
        }

        OAuthGrantType grantType = OAuthGrantType.fromValue(request.getParameter(RESPONSE_TYPE_PARAMETER));

        if (OAuthGrantType.AUTHORIZATION_CODE.equals(grantType)) {
            model.put(STATE_PARAMETER, request.getParameter(STATE_PARAMETER));
            modelAndView.setViewName("redirect:" + request.getParameter(REDIRECT_URI_PARAMETER));
        } else {
            response.setStatus(e.getHttpStatus().value());
        }

        modelAndView.addAllObjects(model);

        return modelAndView;
    }

    /**
     * Landing page for a user to give a 3rd party application permission to their API
     */
    @RequestMapping
    public String authorize(Model model, String client_id, String redirect_uri, String response_type, String scope, String state) throws Exception {

        model.addAttribute("client_id", client_id);
        model.addAttribute("redirect_uri", redirect_uri);
        model.addAttribute("requestedScope", scope);
        model.addAttribute("response_type", response_type);
        model.addAttribute("state", state);

        OAuthGrantType grantType = OAuthGrantType.fromValue(response_type);

        try {
            if (StringUtils.isBlank(client_id) || StringUtils.isBlank(redirect_uri) || StringUtils.isBlank(response_type)) {
                throw new OAuthInvalidRequestException("oauth.exception.authorization.code.invalid.input");
            } else if (grantType != OAuthGrantType.AUTHORIZATION_CODE) {
                throw new OAuthUnsupportedResponseTypeException();
            } else {
                model.addAttribute("oauthApplication", oauthService.fetchApplication(client_id, redirect_uri, response_type));

                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                List<UserAccount> accounts = userService.findSortedUserAccountsByAppType(user, AppType.CRM);

                model.addAttribute("apps", crmService.extractAppNames(accounts));
            }
        } catch (OAuthException e) {
            model.addAttribute("error", e.getErrorDescription());
        }

        return "oauth/" + getViewBase() + "authorize";
    }

    /**
     * Token generation for Resource Grant Type
     */
    @RequestMapping
    @ResponseBody
    public OAuthAccessToken token() throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OAuthAuthenticationToken oAuthAuthenticationToken = (OAuthAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (oAuthAuthenticationToken != null) {
            /**
             * The scope is the application for these grant type
             */
            return oauthService.createAccessToken(oAuthAuthenticationToken.getClientId(), oAuthAuthenticationToken.getClientSecret(), oAuthAuthenticationToken.getGrantType(), oAuthAuthenticationToken.getScope(), oAuthAuthenticationToken.getApplication(), user.getId());
        } else {
            throw new OAuthInvalidRequestException();
        }
    }

    /**
     * Action to grant access to the requesting application
     */
    @RequestMapping
    public String processAuthorization(String allow, String client_id, String redirect_uri, String requestedScope, String application, String state) throws OAuthException {

        if (allow != null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String redirectUriWithCode = oauthService.createAuthorizationCode(client_id, requestedScope, application, redirect_uri, user.getId(), state);

            if (StringUtils.isNotBlank(redirectUriWithCode)) {
                return "redirect:" + redirectUriWithCode;
            } else {
                throw new OAuthServerErrorException();
            }
        } else {
            throw new OAuthAccessDeniedException();
        }
    }

    /**
     * Allows user to view to all apps granted access to their CRM account via oauth.
     */
    @RequestMapping
    public ModelAndView manageAccounts(Long userId, Long infusionsoftAccountId) throws OAuthException {
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
    public void revokeAccess(Long userId, Long infusionsoftAccountId, String masheryAppId) throws OAuthException {
        User user = userService.loadUser(userId);
        UserAccount ua = userService.findUserAccount(user, infusionsoftAccountId);
        oauthService.revokeAccessTokensByUserAccount(ua, masheryAppId);
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
            Set<OAuthUserApplication> userApplications = oauthService.fetchUserApplicationsByUserAccount(userAccount);

            model.addAttribute("userApplications", userApplications);
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
            OAuthAccessToken masheryAccessToken = oauthService.fetchAccessToken(accessToken);

            model.addAttribute("masheryAccessToken", masheryAccessToken);
        }

        return "oauth/viewAccessToken";
    }

}
