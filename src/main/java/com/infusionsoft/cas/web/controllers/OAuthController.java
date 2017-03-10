package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.auth.OAuthAuthenticationToken;
import com.infusionsoft.cas.auth.OAuthExceptionHandler;
import com.infusionsoft.cas.auth.OAuthRefreshAuthenticationToken;
import com.infusionsoft.cas.auth.OAuthResourceOwnerAuthenticationToken;
import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.dto.OAuthAccessToken;
import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import com.infusionsoft.cas.oauth.exceptions.*;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller that provides the required Authorization Server endpoints for OAuth 2.0 Grants
 * These endpoints call the Mashery API to verify clients, obtain authorization codes, and allows the user to accept/deny access.
 */
@Controller
public class OAuthController {

    @Autowired
    CrmService crmService;

    @Autowired
    OAuthService oauthService;

    @Autowired
    UserService userService;

    @Autowired
    OAuthExceptionHandler oAuthExceptionHandler;

    @Value("${mashery.api.crm.service.key}")
    private String crmServiceKey;

    @Value("${cas.viewResolver.basename}")
    private String viewResolverBaseName;

    private String getViewBase() {
        return "default_views".equals(viewResolverBaseName) ? "" : viewResolverBaseName + "/";
    }

    @ExceptionHandler(OAuthException.class)
    public ModelAndView handleOAuthException(OAuthException e, HttpServletRequest request, HttpServletResponse response) {
        return oAuthExceptionHandler.resolveException(request, response, null, e);
    }

    /**
     * Landing page for a user to give a 3rd party application permission to their API
     *
     * @param model         model
     * @param client_id     client_id
     * @param redirect_uri  redirect_uri
     * @param response_type response_type
     * @param scope         scope
     * @param state         state
     * @return view
     * @throws Exception e
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
                model.addAttribute("oauthApplication", oauthService.fetchApplication(crmServiceKey, client_id, redirect_uri, response_type));

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
     * Token generation for Legacy Resource Owner Credential Grant Type
     *
     * @param serviceKey serviceKey
     * @return OAuthAccessToken
     * @throws Exception e
     */
    @ResponseBody
    @RequestMapping("/oauth/service/{serviceKey}/token")
    public OAuthAccessToken token(@PathVariable String serviceKey) throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OAuthAuthenticationToken oAuthAuthenticationToken = (OAuthAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (oAuthAuthenticationToken != null) {
            String refreshToken = null;
            if (oAuthAuthenticationToken instanceof OAuthRefreshAuthenticationToken) {
                refreshToken = ((OAuthRefreshAuthenticationToken) oAuthAuthenticationToken).getRefreshToken();
            }

            if (!userService.validateUserApplication(oAuthAuthenticationToken.getApplication())) {
                throw new OAuthAccessDeniedException();
            }

            /**
             * The scope is the application for these grant type
             */
            return oauthService.createAccessToken(serviceKey, oAuthAuthenticationToken.getClientId(), oAuthAuthenticationToken.getClientSecret(), oAuthAuthenticationToken.getGrantType(), oAuthAuthenticationToken.getScope(), oAuthAuthenticationToken.getApplication(), user.getId().toString(), refreshToken);
        } else {
            throw new OAuthInvalidRequestException();
        }
    }

    /**
     * Token generation for Extended Grant Types
     *
     * @return OAuthAccessToken
     * @throws Exception e
     */
    @ResponseBody
    @RequestMapping("/oauth/token")
    public OAuthAccessToken token() throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OAuthAuthenticationToken token = (OAuthAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (token != null) {
            String userId;
            String refreshToken = null;
            final OAuthServiceConfig serviceConfig = token.getServiceConfig();

            if (token instanceof OAuthRefreshAuthenticationToken) {
                refreshToken = ((OAuthRefreshAuthenticationToken) token).getRefreshToken();
            } else if (token instanceof OAuthResourceOwnerAuthenticationToken) {
                if (StringUtils.equals(serviceConfig.getName(), "crm") && !userService.validateUserApplication(token.getApplication())) {
                    throw new OAuthAccessDeniedException();
                }
            }

            if (principal != null && principal instanceof User) {
                userId = ((User) principal).getId().toString();
            } else {
                userId = (principal == null ? null : principal.toString());
            }

            if (StringUtils.isBlank(userId)) {
                throw new OAuthAccessDeniedException();
            }

            /**
             * The scope is the application for these grant type
             */
            return oauthService.createAccessToken(serviceConfig.getServiceKey(), token.getClientId(), token.getClientSecret(), token.getGrantType(), token.getScope(), token.getApplication(), userId, refreshToken);
        } else {
            throw new OAuthInvalidRequestException();
        }
    }

    /**
     * Action to grant access to the requesting application
     *
     * @param allow          allow
     * @param client_id      client_id
     * @param redirect_uri   redirect_uri
     * @param requestedScope requestedScope
     * @param application    application
     * @param state          state
     * @return view
     * @throws OAuthException e
     */
    @RequestMapping
    public String processAuthorization(String allow, String client_id, String redirect_uri, String requestedScope, String application, String state) throws OAuthException {

        if (allow != null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String redirectUriWithCode = oauthService.createAuthorizationCode(crmServiceKey, client_id, requestedScope, application, redirect_uri, user.getId(), state);

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
     *
     * @param userId                userId
     * @param infusionsoftAccountId infusionsoftAccountId
     * @return ModelAndView
     * @throws OAuthException e
     */
    @RequestMapping
    public ModelAndView manageAccounts(Long userId, Long infusionsoftAccountId) throws OAuthException {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = userService.loadUser(userId);
        UserAccount ua = userService.findUserAccount(user, infusionsoftAccountId);
        model.put("appsGrantedAccess", oauthService.fetchUserApplicationsByUserAccount(crmServiceKey, ua));
        model.put("infusionsoftAccountId", infusionsoftAccountId);

        return new ModelAndView("oauth/manageAccounts", model);
    }

    /**
     * Allows user to revoke access to any app granted access to their CRM account via oauth.
     *
     * @param userId                userId
     * @param infusionsoftAccountId infusionsoftAccountId
     * @param masheryAppId          masheryAppId
     * @throws OAuthException e
     */
    @RequestMapping
    public void revokeAccess(Long userId, Long infusionsoftAccountId, String masheryAppId) throws OAuthException {
        User user = userService.loadUser(userId);
        UserAccount ua = userService.findUserAccount(user, infusionsoftAccountId);
        oauthService.revokeAccessTokensByUserAccount(crmServiceKey, ua, masheryAppId);
    }
}
