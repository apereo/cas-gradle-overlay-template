package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.CommunityAccountDetails;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.AppCredentialsExpiredException;
import com.infusionsoft.cas.exceptions.AppCredentialsInvalidException;
import com.infusionsoft.cas.exceptions.UsernameTakenException;
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
import java.util.HashMap;
import java.util.Map;

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

    @Value("${infusionsoft.cas.central.promptToAssociate}")
    boolean promptToAssociate = false;

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

    /**
     * Renders the Infusionsoft Central home page.
     */
    @RequestMapping
    public String home(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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
        model.addAttribute("accounts", userService.findSortedUserAccounts(user));
        model.addAttribute("connectAccountCrmEnabled", connectAccountCrmEnabled);
        model.addAttribute("connectAccountCommunityEnabled", connectAccountCommunityEnabled);
        model.addAttribute("connectAccountCustomerHubEnabled", connectAccountCustomerHubEnabled);

        return "central/home";
    }

    /**
     * Displays the form to link up an existing Infusionsoft CRM account.
     */
    @RequestMapping
    public ModelAndView linkInfusionsoftAppAccount() {
        return new ModelAndView("central/linkInfusionsoftAppAccount", "crmDomain", crmDomain);
    }

    /**
     * Displays the form to link up an existing CustomerHub account.
     */
    @RequestMapping
    public ModelAndView linkCustomerHubAccount() {
        return new ModelAndView("central/linkCustomerHubAccount", "customerHubDomain", customerHubDomain);
    }

    /**
     * Displays the form to link up an existing community account.
     */
    @RequestMapping
    public ModelAndView linkCommunityAccount() {
        return new ModelAndView("central/linkCommunityAccount");
    }

    /**
     * Unlinks an account.
     */
    @RequestMapping
    public ModelAndView unlinkAccount(Long account) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccount userAccount = userService.findUserAccount(user, account);

        userService.disableAccount(userAccount);

        return new ModelAndView("redirect:/central/home");
    }

    /**
     * Creates a brand new community account and associates it to the CAS account.
     */
    @RequestMapping()
    public ModelAndView createCommunityAccount(Boolean agreeToRules, String displayName, Integer infusionsoftExperience, String timeZone, String notificationEmailAddress, String twitterHandle) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommunityAccountDetails details = new CommunityAccountDetails();

        details.setNotificationEmailAddress(user.getUsername());

        model.put("infusionsoftExperienceLevels", new int[]{1, 2, 3, 4, 5});
        model.put("details", details);

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

                return new ModelAndView("redirect:home");
            } catch (UsernameTakenException e) {
                log.error("failed to register community account for user " + user.getId(), e);

                model.put("error", "community.error.displayNameTaken");
            } catch (Exception e) {
                log.error("unexpected error while registering community account for user " + user.getId(), e);

                model.put("error", "community.error.unknown");
            }
        }

        return new ModelAndView("central/createCommunityAccount", model);
    }

    /**
     * Associates the current user to a legacy account, after first validating the legacy username and password.
     */
    @RequestMapping
    public ModelAndView associate(String appType, String appName, String appUsername, String appPassword, String cancel, String destination, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        String sanitizedAppName = ValidationUtils.sanitizeAppName(appName);

        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (cancel != null) {
                return new ModelAndView("redirect:home");
            } else if (StringUtils.isEmpty(appUsername)) {
                model.put("connectError", "registration.error.invalidAppUsername");
            } else if (StringUtils.isEmpty(appPassword)) {
                model.put("connectError", "registration.error.invalidPassword");
            } else if (appType.equals(AppType.COMMUNITY)) {
                String communityUserId = communityService.authenticateUser(appUsername, appPassword);

                sanitizedAppName = "community";

                if (StringUtils.isNotEmpty(communityUserId)) {
                    userService.associateAccountToUser(user, appType, sanitizedAppName, communityUserId);
                } else {
                    model.put("connectError", "registration.error.invalidLegacyCredentials");
                }
            } else if (appType.equals(AppType.CRM) && !crmService.isCasEnabled(sanitizedAppName)) {
                model.put("connectError", "registration.error.ssoIsNotEnabled");
            } else {
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
            }
            model.put("appDomain", new URL(appHelper.buildAppUrl(appType, sanitizedAppName)).getHost());
            model.put("appUrl", appHelper.buildAppUrl(appType, sanitizedAppName));
        } catch (Exception e) {
            log.error("failed to associate account", e);

            model.put("connectError", "registration.error.couldNotAssociate");
        }

        if (model.containsKey("connectError")) {
            if (appType.equals(AppType.CRM)) {
                model.put("crmDomain", crmDomain);

                return new ModelAndView("central/linkInfusionsoftAppAccount", model);
            } else if (appType.equals(AppType.CUSTOMERHUB)) {
                model.put("customerHubDomain", customerHubDomain);

                return new ModelAndView("central/linkCustomerHubAccount", model);
            } else if (appType.equals(AppType.COMMUNITY)) {
                return new ModelAndView("central/linkCommunityAccount", model);
            } else {
                throw new Exception("Failed to associate");
            }
        } else if (StringUtils.equals("app", destination)) {
            return new ModelAndView("redirect:" + model.get("appUrl"));
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
            userService.updateUserAccount(account);

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

        if (passwordService.isPasswordValid(user.getUsername(), currentPassword)) {
            response.setContentType("text/plain");
            response.getWriter().write("OK");
        } else {
            log.info("existing password is incorrect for user " + user.getId());
            response.sendError(401);
        }

        return null;
    }
}
