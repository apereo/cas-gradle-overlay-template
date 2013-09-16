package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.CommunityAccountDetails;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.AppCredentialsExpiredException;
import com.infusionsoft.cas.exceptions.AppCredentialsInvalidException;
import com.infusionsoft.cas.exceptions.CommunityUsernameTakenException;
import com.infusionsoft.cas.exceptions.DuplicateAccountException;
import com.infusionsoft.cas.oauth.MasheryService;
import com.infusionsoft.cas.oauth.domain.MasheryUserApplication;
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
import java.net.URL;
import java.util.*;

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
    private MasheryService masheryService;

    @Value("${infusionsoft.cas.connect.account.crm.enabled}")
    boolean connectAccountCrmEnabled = false;

    @Value("${infusionsoft.cas.connect.account.community.enabled}")
    boolean connectAccountCommunityEnabled = false;

    @Value("${infusionsoft.cas.connect.account.customerhub.enabled}")
    boolean connectAccountCustomerHubEnabled = false;

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

    @Value("${mashery.service.key}")
    private String serviceKey;

    /**
     * Allows user to view to all apps granted access to their CRM account via oauth.
     */
    @RequestMapping
    public ModelAndView manageAccounts(Long userId, Long infusionsoftAccountId) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = userService.loadUser(userId);
        UserAccount ua = userService.findUserAccount(user, infusionsoftAccountId);
        model.put("appsGrantedAccess", masheryService.fetchUserApplicationsByUserAccount(ua));
        model.put("infusionsoftAccountId", infusionsoftAccountId);
        return new ModelAndView("central/manageAccounts", model);
    }

    /**
     * Allows user to revoke access to any app granted access to their CRM account via oauth.
     */
    @RequestMapping
    public ModelAndView revokeAccess(HttpServletResponse response, Long userId, Long infusionsoftAccountId, Long masheryAppId) throws IOException {
        User user = userService.loadUser(userId);
        UserAccount ua = userService.findUserAccount(user, infusionsoftAccountId);
        Set<MasheryUserApplication> masheryUserApplications = masheryService.fetchUserApplicationsByUserAccount(ua);
        for (MasheryUserApplication ma : masheryUserApplications) {
            if (masheryAppId == Long.parseLong(ma.getId())) {
                for (String accessToken : ma.getAccess_tokens()) {
                    try {
                        masheryService.revokeAccessToken(serviceKey, ma.getClient_id(), accessToken);
                    } catch (Exception e) {
                        log.error("Failed to revoke app access for app= " + ma.getName(), e);
                        response.sendError(500);
                    }
                }
                break;
            }
        }
        return null;
    }

    /**
     * Renders the Infusionsoft Central home page.
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

        List<UserAccount> userAccountList = userService.findSortedUserAccounts(user);

        model.addAttribute("accounts", userAccountList);
        model.addAttribute("connectAccountCrmEnabled", connectAccountCrmEnabled);
        model.addAttribute("connectAccountCommunityEnabled", connectAccountCommunityEnabled);
        model.addAttribute("connectAccountCustomerHubEnabled", connectAccountCustomerHubEnabled);

        //logUserAccountInfoToSplunk(userAccountList, user);

        return "central/home";
    }

    /**
     * Displays the form to link up an existing Infusionsoft CRM account.
     */
    @RequestMapping
    public String linkInfusionsoftAppAccount(Model model) {
        model.addAttribute("crmDomain", crmDomain);
        model.addAttribute("appType", AppType.CRM);

        return "central/linkInfusionsoftAppAccount";
    }

    /**
     * Displays the form to link up an existing CustomerHub account.
     */
    @RequestMapping
    public String linkCustomerHubAccount(Model model) {
        model.addAttribute("customerHubDomain", customerHubDomain);
        model.addAttribute("appType", AppType.CUSTOMERHUB);

        return "central/linkCustomerHubAccount";
    }

    /**
     * Displays the form to link up an existing community account.
     */
    @RequestMapping
    public String linkCommunityAccount(Model model) {
        model.addAttribute("appType", AppType.COMMUNITY);

        return "central/linkCommunityAccount";
    }

    /**
     * Unlinks an account.
     */
    @RequestMapping
    public ModelAndView unlinkAccount(Long account) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount userAccount = userService.findUserAccount(user, account);

        userService.disableAccount(userAccount);
        masheryService.revokeAccessTokensByUserAccount(userAccount);
        return new ModelAndView("redirect:/central/home");
    }

    /**
     * Creates a brand new community account and associates it to the CAS account.
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
                } else if (AppType.CRM.equals(appType) && !crmService.isCasEnabled(sanitizedAppName)) {
                    model.put("connectError", "registration.error.ssoIsNotEnabled");
                } else if (AppType.CRM.equals(appType) || AppType.CUSTOMERHUB.equals(appType)) {
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
            if (AppType.CRM.equals(appType)) {
                String appUrl = appHelper.buildAppUrl(appType, sanitizedAppName);
                model.put("crmDomain", crmDomain);
                model.put("appDomain", new URL(appUrl).getHost());
                model.put("appUrl", appUrl);
                return new ModelAndView("central/linkInfusionsoftAppAccount", model);
            } else if (AppType.CUSTOMERHUB.equals(appType)) {
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
     * Called from the AJAX quick edit to rename an account alias.
     */
    @RequestMapping
    public ModelAndView renameAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long accountId = new Long(request.getParameter("id"));
        String alias = ValidationUtils.sanitizeAppAlias(request.getParameter("value"));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount account = userService.findUserAccount(user, accountId);

        try {
            account.setAlias(alias);
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

    private void logUserAccountInfoToSplunk(List<UserAccount> userAccountList, User user) {
        int payingAccounts = 0;
        for (UserAccount account : userAccountList) {
            if (account.getAppType().equals(AppType.CRM) || account.getAppType().equals(AppType.CUSTOMERHUB)) {
                payingAccounts++;
            }
        }
        //add user id so we can search by unique and eliminate dups
        if (payingAccounts == 1) {
            log.error("User has 1 paying account. User=" + user.getId());
        } else if (payingAccounts > 1) {
            log.error("User has > 1 paying accounts. User=" + user.getId());
        }
    }
}
