package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.CommunityAccountDetails;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.AppCredentialsExpiredException;
import com.infusionsoft.cas.exceptions.AppCredentialsInvalidException;
import com.infusionsoft.cas.exceptions.CommunityUsernameTakenException;
import com.infusionsoft.cas.exceptions.DuplicateAccountException;
import com.infusionsoft.cas.oauth.dto.OAuthUserApplication;
import com.infusionsoft.cas.oauth.exceptions.OAuthAccessDeniedException;
import com.infusionsoft.cas.oauth.exceptions.OAuthException;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.*;
import com.infusionsoft.cas.support.AppHelper;
import com.infusionsoft.cas.web.ValidationUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
 * Controller that powers the central "hub" along with account association and profile management.
 */
@Controller
public class CentralController {
    private static final Logger log = Logger.getLogger(CentralController.class);

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    CommunityService communityService;

    @Autowired
    CrmService crmService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordService passwordService;

    @Autowired
    AppHelper appHelper;

    @Autowired
    AutoLoginService autoLoginService;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private SupportContactService supportContactService;

    @Value("${infusionsoft.cas.connect.account.community.enabled}")
    boolean connectAccountCommunityEnabled = false;

    @Value("${infusionsoft.cas.connect.account.customerhub.enabled}")
    boolean connectAccountCustomerHubEnabled = false;

    @Value("${mashery.api.crm.service.key}")
    private String crmServiceKey;

    @Value("${server.prefix}")
    String serverPrefix;

    @Value("${infusionsoft.crm.protocol}")
    String crmProtocol;

    @Value("${infusionsoft.crm.domain}")
    String crmDomain;

    @Value("${infusionsoft.crm.port}")
    String crmPort;

    @Value("${infusionsoft.customerhub.domain}")
    String customerHubDomain;

    @Value("${infusionsoft.community.domain}")
    String communityDomain;

    @Value("${infusionsoft.marketplace.domain}")
    String marketplaceDomain;

    @Value("${infusionsoft.marketplace.loginurl}")
    String marketplaceLoginUrl;

    /**
     * Renders the Infusionsoft Central home page.
     *
     * @param model        model
     * @param connectError connectError
     * @return view
     */
    @RequestMapping
    public String home(Model model, String connectError) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("connectError", ValidationUtils.sanitizeMessageCode(connectError));
        model.addAttribute("user", user);
        model.addAttribute("homeLinkSelected", "selected");
        model.addAttribute("hasCommunityAccount", infusionsoftAuthenticationService.hasCommunityAccount(user));
        model.addAttribute("crmDomain", crmDomain);
        model.addAttribute("crmProtocol", crmProtocol);
        model.addAttribute("crmPort", crmPort);
        model.addAttribute("communityDomain", communityDomain);
        model.addAttribute("customerHubDomain", customerHubDomain);
        model.addAttribute("marketplaceDomain", marketplaceDomain);
        model.addAttribute("marketplaceUrl", marketplaceLoginUrl);

        Map<AppType, List<UserAccount>> userAccountList = userService.findSortedUserAccounts(user);
        model.addAttribute("accounts", userAccountList);
        model.addAttribute("connectAccountCommunityEnabled", connectAccountCommunityEnabled);
        model.addAttribute("connectAccountCustomerHubEnabled", connectAccountCustomerHubEnabled);

        return "central/home";
    }

    /**
     * Displays the form to link up an existing CustomerHub account.
     *
     * @param model model
     * @return view
     */
    @RequestMapping
    public String linkCustomerHubAccount(Model model) {
        model.addAttribute("customerHubDomain", customerHubDomain);
        model.addAttribute("appType", AppType.CUSTOMERHUB);

        return "central/linkCustomerHubAccount";
    }

    /**
     * Displays the form to link up an existing community account.
     *
     * @param model model
     * @return view
     */
    @RequestMapping
    public String linkCommunityAccount(Model model) {
        model.addAttribute("appType", AppType.COMMUNITY);

        return "central/linkCommunityAccount";
    }

    /**
     * Creates a brand new community account and associates it to the CAS account.
     *
     * @param agreeToRules             agreeToRules
     * @param displayName              displayName
     * @param infusionsoftExperience   infusionsoftExperience
     * @param timeZone                 timeZone
     * @param notificationEmailAddress notificationEmailAddress
     * @param twitterHandle            twitterHandle
     * @param request                  request
     * @param response                 response
     * @return ModelAndView
     */
    @RequestMapping()
    public ModelAndView createCommunityAccount(Boolean agreeToRules, String displayName, Integer infusionsoftExperience, String timeZone, String notificationEmailAddress, String twitterHandle, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommunityAccountDetails details = new CommunityAccountDetails();

        details.setNotificationEmailAddress(user.getUsername());

        model.put("communityUrl", communityService.getBaseUrl());
        model.put("infusionsoftExperienceLevels", new int[]{1, 2, 3, 4, 5});
        model.put("details", details);
        model.put("supportPhoneNumbers", supportContactService.getSupportPhoneNumbers());

        // Only process this section if at least one thing is filled out
        if (agreeToRules != null || displayName != null || infusionsoftExperience != null || timeZone != null || notificationEmailAddress != null || twitterHandle != null) {
            details.setDisplayName(displayName);
            details.setInfusionsoftExperience(infusionsoftExperience);
            details.setTimeZone(timeZone);
            details.setNotificationEmailAddress(notificationEmailAddress);
            details.setTwitterHandle(twitterHandle);

            if (StringUtils.isEmpty(details.getDisplayName()) || details.getDisplayName().length() < 4 || details.getDisplayName().length() > 30) {
                model.put("error", "community.error.displayNameInvalid");
            } else if (StringUtils.isEmpty(details.getNotificationEmailAddress()) || !EmailValidator.getInstance().isValid(details.getNotificationEmailAddress())) {
                model.put("error", "community.error.notificationEmailAddressInvalid");
            } else if (!agreeToRules) {
                model.put("error", "community.error.agreeToRules");
            }

            if (!model.containsKey("error")) {
                log.info("attempting to register a forum account for user " + user.getId());

                try {
                    communityService.registerCommunityUserAccount(user, details);
                    autoLoginService.autoLogin(user.getUsername(), request, response);

                    return new ModelAndView("redirect:home");
                } catch (CommunityUsernameTakenException e) {
                    log.error("failed to register community account for user " + user.getId(), e);

                    model.put("error", "community.error.displayNameTaken");
                } catch (Exception e) {
                    log.error("unexpected error while registering community account for user " + user.getId(), e);

                    model.put("error", "community.error.unknown");
                }
            }
        }

        return new ModelAndView("central/createCommunityAccount", model);
    }

    /**
     * Associates the current user to a legacy account, after first validating the legacy username and password.
     *
     * @param appType     appType
     * @param appName     appName
     * @param appUsername appUsername
     * @param appPassword appPAssword
     * @param request     request
     * @param response    response
     * @return ModelAndView
     * @throws Exception e
     */
    @RequestMapping
    public ModelAndView associate(AppType appType, String appName, String appUsername, String appPassword, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        String sanitizedAppName = ValidationUtils.sanitizeAppName(appName);

        try {
            if (StringUtils.isEmpty(appUsername)) {
                model.put("connectError", "registration.error.invalidAppUsername");
            } else if (StringUtils.isEmpty(appPassword)) {
                model.put("connectError", "password.error.blank");
            } else if (appType == null) {
                model.put("connectError", "registration.error.couldNotAssociate");
            } else {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (AppType.COMMUNITY.equals(appType)) {
                    String communityUserId = communityService.authenticateUser(appUsername, appPassword);

                    sanitizedAppName = "community";

                    if (StringUtils.isNotEmpty(communityUserId)) {
                        userService.associateAccountToUser(user, appType, sanitizedAppName, communityUserId);
                        autoLoginService.autoLogin(user.getUsername(), request, response);
                    } else {
                        model.put("connectError", "registration.error.invalidLegacyCredentials");
                    }
                } else if (AppType.CUSTOMERHUB.equals(appType)) {
                    try {
                        try {
                            infusionsoftAuthenticationService.verifyAppCredentials(appType, sanitizedAppName, appUsername, appPassword);
                        } catch (AppCredentialsExpiredException e) {
                            log.info("accepting expired credentials for " + appUsername + " at " + sanitizedAppName + "/" + appType);
                        }

                        userService.associateAccountToUser(user, appType, sanitizedAppName, appUsername);
                        autoLoginService.autoLogin(user.getUsername(), request, response);
                    } catch (AppCredentialsInvalidException e) {
                        model.put("connectError", "registration.error.invalidLegacyCredentials");
                    }
                } else {
                    model.put("connectError", "registration.error.couldNotAssociate");
                }
            }
        } catch (DuplicateAccountException e) {
            model.put("connectError", "registration.error.alreadyLinked");
        } catch (Exception e) {
            log.error("failed to associate account for appType " + appType, e);
            model.put("connectError", "registration.error.couldNotAssociate");
        }

        if (model.containsKey("connectError")) {
            model.put("appType", appType);
            model.put("appName", sanitizedAppName);
            model.put("appUsername", appUsername);
            if (AppType.CUSTOMERHUB.equals(appType)) {
                model.put("customerHubDomain", customerHubDomain);
                return new ModelAndView("central/linkCustomerHubAccount", model);
            } else if (AppType.COMMUNITY.equals(appType)) {
                return new ModelAndView("central/linkCommunityAccount", model);
            } else {
                return new ModelAndView("redirect:home", model);
            }
        } else {
            return new ModelAndView("redirect:home");
        }
    }

    /**
     * Called from the AJAX Configure Account
     *
     * @param model     model
     * @param accountId accountId
     * @return view
     * @throws com.infusionsoft.cas.oauth.exceptions.OAuthException e
     * @throws IOException                                          e
     */
    @RequestMapping
    public String loggedInUserOAuthApplications(Model model, long accountId) throws OAuthException, IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount userAccount = userService.findUserAccount(user, accountId);
        Set<OAuthUserApplication> userApplications = oAuthService.fetchUserApplicationsByUserAccount(crmServiceKey, userAccount);

        model.addAttribute("accountId", accountId);
        model.addAttribute("userApplications", userApplications);

        return "central/loggedInUserOAuthApplications";
    }

    @RequestMapping
    public void revokeAccessToken(Model model, HttpServletResponse response, long accountId, String clientId) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (StringUtils.isNotBlank(clientId)) {
            UserAccount userAccount = userService.findUserAccount(user, accountId);

            try {
                Set<OAuthUserApplication> userApplications = oAuthService.fetchUserApplicationsByUserAccount(crmServiceKey, userAccount);

                for (OAuthUserApplication oAuthUserApplication : userApplications) {
                    if (StringUtils.isBlank(clientId) || clientId.equals(oAuthUserApplication.getClientId())) {
                        for (String accessToken : oAuthUserApplication.getAccessTokens()) {
                            oAuthService.revokeAccessToken(crmServiceKey, clientId, accessToken);
                        }
                    }
                }

                model.addAttribute("success", "Access tokens revoked");
            } catch (OAuthException e) {
                response.sendError(500);
                model.addAttribute("error", e.getErrorDescription());
            }
        }
    }

    /**
     * Called from the AJAX quick edit to rename an account alias.
     *
     * @param accountId accountId
     * @param alias     alias
     * @param response  response
     * @return ModelAndView
     * @throws IOException e
     */
    @RequestMapping
    public ModelAndView renameAccount(Long accountId, String alias, HttpServletResponse response) throws IOException {
        String sanitizeAppAlias = ValidationUtils.sanitizeAppAlias(alias);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount account = userService.findUserAccount(user, accountId);

        try {
            account.setAlias(sanitizeAppAlias);
            userService.saveUserAccount(account);

            response.setContentType("text/plain");
            response.getWriter().write(StringEscapeUtils.escapeHtml4(account.getAlias()));
        } catch (Exception e) {
            log.error("failed to update alias for account " + accountId, e);

            response.sendError(500);
        }

        return null;
    }

    /**
     * Called from AJAX to validate the existing password.
     *
     * @param currentPassword currentPassword
     * @param response        response
     * @return ModelAndView
     * @throws IOException e
     */
    @RequestMapping
    public ModelAndView verifyExistingPassword(String currentPassword, HttpServletResponse response) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (passwordService.isPasswordCorrect(user, currentPassword)) {
            response.setContentType("text/plain");
            response.getWriter().write("OK");
        } else {
            log.info("existing password is incorrect for user " + user.getId());
            response.sendError(401);
        }

        return null;
    }
}
