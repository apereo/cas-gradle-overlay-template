package com.infusionsoft.cas.web;

import com.infusionsoft.cas.exceptions.UsernameTakenException;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.InfusionsoftDataService;
import com.infusionsoft.cas.services.InfusionsoftMailService;
import com.infusionsoft.cas.services.InfusionsoftPasswordService;
import com.infusionsoft.cas.types.AppType;
import com.infusionsoft.cas.types.CommunityAccountDetails;
import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller that powers the central "hub" along with account association and profile management.
 */
public class CentralMultiActionController extends MultiActionController {
    private static final Logger log = Logger.getLogger(CentralMultiActionController.class);

    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private InfusionsoftDataService infusionsoftDataService;
    private InfusionsoftPasswordService infusionsoftPasswordService;
    private InfusionsoftMailService infusionsoftMailService;
    private HibernateTemplate hibernateTemplate;

    /**
     * Gatekeeper that checks if the requested service is associated. If it's an unassociated app,
     * redirects the user to a page where they can link it up. If it's already pending association, make
     * the association and then redirect to a landing page. Otherwise, simply redirect to the home page.
     */
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        HttpSession session = request.getSession(true);
        String service = (String) session.getAttribute("serviceUrl");
        String registrationCode = (String) session.getAttribute("registrationCode");

        if (infusionsoftPasswordService.isPasswordExpired(user)) {
            log.info("user " + user.getId() + " has an expired password! let's make them reset it");

            return new ModelAndView("redirect:passwordExpired");
        } else if (StringUtils.isNotEmpty(registrationCode)) {
            log.info("new user! registration code is " + registrationCode);

            try {
                UserAccount account = infusionsoftDataService.associatePendingAccountToUser(user, registrationCode);

                request.getSession().removeAttribute("registrationCode");
                infusionsoftAuthenticationService.createTicketGrantingTicket(account.getAppUsername(), request, response);

                return new ModelAndView("redirect:" + infusionsoftAuthenticationService.buildAppUrl(account.getAppType(), account.getAppName()));
            } catch (Exception e) {
                log.error("failed to associate new user to registration code " + registrationCode, e);
            }
        } else if (StringUtils.isNotEmpty(service)) {
            String appName = infusionsoftAuthenticationService.guessAppName(new URL(service));
            String appType = infusionsoftAuthenticationService.guessAppType(new URL(service));

            if (infusionsoftAuthenticationService.isAppAssociated(user, new URL(service))) {
                log.info("user " + user.getId() + " is associated with app: " + service + "; redirecting to " + appName + "/" + appType);

                session.removeAttribute("serviceUrl"); // to prevent stale tickets being reused

                return new ModelAndView("redirect:" + service);
            } else if (appName != null && appType != null) {
                log.info("user " + user.getId() + " was referred from an unassociated app " + appName + "/" + appType + "(" + service + ")");

                Map<String, Object> model = new HashMap<String, Object>();

                model.put("appName", appName);
                model.put("appType", appType);

                return new ModelAndView("redirect:linkReferer", model);
            } else {
                log.info("user " + user.getId() + " will be redirected to the home page");

                return new ModelAndView("redirect:home");
            }
        }

        return new ModelAndView("redirect:home");
    }

    /**
     * Renders the Infusionsoft Central home page.
     */
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = infusionsoftAuthenticationService.getCurrentUser(request);

        if (user != null) {
            model.put("user", user);
            model.put("homeLinkSelected", "selected");
            model.put("hasCommunityAccount", infusionsoftAuthenticationService.hasCommunityAccount(user));
            model.put("crmDomain", infusionsoftAuthenticationService.getCrmDomain());
            model.put("crmProtocol", infusionsoftAuthenticationService.getCrmProtocol());
            model.put("crmPort", infusionsoftAuthenticationService.getCrmPort());
            model.put("communityDomain", infusionsoftAuthenticationService.getCommunityDomain());
            model.put("customerHubDomain", infusionsoftAuthenticationService.getCustomerHubDomain());
            model.put("accounts", infusionsoftDataService.findSortedUserAccounts(user));

            return new ModelAndView("infusionsoft/ui/central/home", model);
        } else {
            log.warn("anonymous user visited Infusionsoft Central; logging them out to be safe");

            request.getSession(true).invalidate();

            return new ModelAndView("redirect:/logout", model);
        }
    }

    /**
     * Displays the form to link up an existing Infusionsoft CRM account.
     */
    public ModelAndView linkInfusionsoftAppAccount(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("infusionsoft/ui/central/linkInfusionsoftAppAccount", "crmDomain", infusionsoftAuthenticationService.getCrmDomain());
    }

    /**
     * Displays the form to link up an existing CustomerHub account.
     */
    public ModelAndView linkCustomerHubAccount(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("infusionsoft/ui/central/linkCustomerHubAccount", "customerHubDomain", infusionsoftAuthenticationService.getCustomerHubDomain());
    }

    /**
     * Displays the form to link up an existing community account.
     */
    public ModelAndView linkCommunityAccount(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("infusionsoft/ui/central/linkCommunityAccount");
    }

    /**
     * Unlinks an account.
     */
    public ModelAndView unlinkAccount(HttpServletRequest request, HttpServletResponse response) {
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        UserAccount account = infusionsoftDataService.findUserAccount(user, Long.parseLong(request.getParameter("account")));

        infusionsoftDataService.disableAccount(account);

        return new ModelAndView("redirect:/central/home");
    }

    /**
     * Displays a short form to get legacy app credentials before linking up the referring app.
     */
    public ModelAndView linkReferer(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();

        model.put("appName", request.getParameter("appName"));
        model.put("appType", request.getParameter("appType"));
        model.put("appDomain", request.getParameter("appName") + "." + infusionsoftAuthenticationService.getCrmDomain());

        return new ModelAndView("infusionsoft/ui/central/linkReferer", model);
    }

    /**
     * Creates a brand new community account and associates it to the CAS account.
     */
    public ModelAndView createCommunityAccount(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        CommunityAccountDetails details = new CommunityAccountDetails();

        details.setNotificationEmailAddress(user.getUsername());

        model.put("infusionsoftExperienceLevels", new int[]{1, 2, 3, 4, 5});
        model.put("details", details);

        if (request.getMethod().equalsIgnoreCase("POST")) {
            boolean agreeToRules = Boolean.valueOf(request.getParameter("agreeToRules"));

            details.setDisplayName(request.getParameter("displayName"));
            details.setInfusionsoftExperience(Integer.parseInt(request.getParameter("infusionsoftExperience")));
            details.setTimeZone(request.getParameter("timeZone"));
            details.setNotificationEmailAddress(request.getParameter("notificationEmailAddress"));
            details.setTwitterHandle(request.getParameter("twitterHandle"));

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
                    infusionsoftAuthenticationService.registerCommunityUserAccount(user, details);
                    infusionsoftAuthenticationService.createTicketGrantingTicket(user.getUsername(), request, response);

                    return new ModelAndView("redirect:index");
                } catch (UsernameTakenException e) {
                    log.error("failed to register community account for user " + user.getId(), e);

                    model.put("error", "community.error.displayNameTaken");
                } catch (Exception e) {
                    log.error("unexpected error while registering community account for user " + user.getId(), e);

                    model.put("error", "community.error.unknown");
                }
            }
        }

        return new ModelAndView("infusionsoft/ui/central/createCommunityAccount", model);
    }

    /**
     * Associates the current user to a legacy account, after first validating the legacy username and password.
     */
    public ModelAndView associate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        String appType = request.getParameter("appType");
        String appName = request.getParameter("appName").toLowerCase();
        String appUsername = request.getParameter("appUsername").toLowerCase();
        String appPassword = request.getParameter("appPassword");

        try {
            User currentUser = infusionsoftAuthenticationService.getCurrentUser(request);

            if (request.getParameter("cancel") != null) {
                request.getSession(true).removeAttribute("serviceUrl");

                return new ModelAndView("redirect:home");
            } else if (StringUtils.isEmpty(appUsername)) {
                model.put("error", "registration.error.invalidAppUsername");
            } else if (StringUtils.isEmpty(appPassword)) {
                model.put("error", "registration.error.invalidPassword");
            } else if (appType.equals(AppType.COMMUNITY)) {
                String communityUserId = infusionsoftAuthenticationService.verifyCommunityCredentials(appUsername, appPassword);

                appName = "community";

                if (StringUtils.isNotEmpty(communityUserId)) {
                    infusionsoftDataService.associateAccountToUser(currentUser, appType, appName, communityUserId);
                    infusionsoftAuthenticationService.createTicketGrantingTicket(currentUser.getUsername(), request, response);
                } else {
                    model.put("error", "registration.error.invalidLegacyCredentials");
                }
            } else if (infusionsoftAuthenticationService.verifyAppCredentials(appType, appName, appUsername, appPassword)) {
                infusionsoftDataService.associateAccountToUser(currentUser, appType, appName, appUsername);
                infusionsoftAuthenticationService.createTicketGrantingTicket(currentUser.getUsername(), request, response);
            } else {
                model.put("error", "registration.error.invalidLegacyCredentials");
            }

            model.put("appUrl", infusionsoftAuthenticationService.buildAppUrl(appType, appName));
        } catch (Exception e) {
            log.error("failed to associate account", e);

            model.put("error", "registration.error.couldNotAssociate");
        }

        if (model.containsKey("error")) {
            if (request.getParameter("linkReferer") != null) {
                model.put("appName", request.getParameter("appName"));
                model.put("appType", request.getParameter("appType"));
                model.put("appDomain", request.getParameter("appName") + "." + infusionsoftAuthenticationService.getCrmDomain());

                return new ModelAndView("infusionsoft/ui/central/linkReferer", model);
            } else if (appType.equals(AppType.CRM)) {
                model.put("crmDomain", infusionsoftAuthenticationService.getCrmDomain());

                return new ModelAndView("infusionsoft/ui/central/linkInfusionsoftAppAccount", model);
            } else if (appType.equals(AppType.CUSTOMERHUB)) {
                model.put("customerHubDomain", infusionsoftAuthenticationService.getCustomerHubDomain());

                return new ModelAndView("infusionsoft/ui/central/linkCustomerHubAccount", model);
            } else if (appType.equals(AppType.COMMUNITY)) {
                return new ModelAndView("infusionsoft/ui/central/linkCommunityAccount", model);
            } else {
                response.sendError(500, "Failed to associate");
            }

            return null;
        } else if (StringUtils.equals("app", request.getParameter("destination"))) {
            return new ModelAndView("redirect:" + model.get("appUrl"));
        } else {
            return new ModelAndView("redirect:home");
        }
    }

    /**
     * Brings up the form to edit the user profile.
     */
    public ModelAndView editProfile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<String, Object>();

            model.put("user", infusionsoftAuthenticationService.getCurrentUser(request));
            model.put("editProfileLinkSelected", "selected");

            return new ModelAndView("infusionsoft/ui/central/editProfile", model);
        } catch (Exception e) {
            log.error("unable to load user for current request!", e);

            return new ModelAndView("redirect:home");
        }
    }

    /**
     * Updates the user profile.
     */
    public ModelAndView updateProfile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String username = request.getParameter("username");
        String existingPassword = request.getParameter("currentPassword");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        Map<String, Object> model = new HashMap<String, Object>();

        try {
            User user = hibernateTemplate.get(User.class, new Long(request.getParameter("id")));

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);

            model.put("user", user);

            if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.put("error", "editprofile.error.invalidUsername");
            } else if (hibernateTemplate.find("from User u where u.username = ? and u.id != ?", username, user.getId()).size() > 0) {
                model.put("error", "editprofile.error.usernameInUse");
            } else if (!infusionsoftPasswordService.isPasswordValid(user, existingPassword)) {
                model.put("error", "editprofile.error.incorrectCurrentPassword");
            } else if (StringUtils.isNotEmpty(password1) || StringUtils.isNotEmpty(password2)) {
                String passwordError = infusionsoftPasswordService.validatePassword(user, username, password1);

                if (passwordError != null) {
                    model.put("error", passwordError);
                }
            }

            if (model.containsKey("error")) {
                log.info("couldn't update user account for user " + user.getId() + ": " + model.get("error"));
            } else {
                if (StringUtils.isNotEmpty(password1)) {
                    infusionsoftPasswordService.setPasswordForUser(user, password1);
                }

                hibernateTemplate.update(user);

                infusionsoftAuthenticationService.createTicketGrantingTicket(username, request, response);
            }
        } catch (Exception e) {
            log.error("failed to update user account", e);

            model.put("error", "editprofile.error.exception");
        }

        if (model.containsKey("error")) {
            return new ModelAndView("infusionsoft/ui/central/editProfile", model);
        } else {
            return new ModelAndView("redirect:home");
        }
    }

    /**
     * Shows a form demanding that the user reset their expired password.
     */
    public ModelAndView passwordExpired(HttpServletRequest request, HttpServletResponse response) {
        User user = infusionsoftAuthenticationService.getCurrentUser(request);

        return new ModelAndView("infusionsoft/ui/central/passwordExpired", "user", user);
    }

    public ModelAndView updatePassword(HttpServletRequest request, HttpServletResponse response) {
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        Map<String, Object> model = new HashMap<String, Object>();

        if (StringUtils.isEmpty(password1) || StringUtils.isEmpty(password2)) {
            model.put("error", "registration.error.invalidPassword");
        } else if (!password1.equals(password2)) {
            model.put("error", "registration.error.passwordsNoMatch");
        } else {
            String passwordError = infusionsoftPasswordService.validatePassword(user, user.getUsername(), password1);

            if (passwordError != null) {
                model.put("error", passwordError);
            }
        }

        if (model.containsKey("error")) {
            return new ModelAndView("infusionsoft/ui/central/passwordExpired", model);
        } else {
            infusionsoftPasswordService.setPasswordForUser(user, password1);

            return new ModelAndView("redirect:index");
        }
    }

    /**
     * Called from the AJAX quick edit to rename an account alias.
     */
    public ModelAndView renameAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long accountId = new Long(request.getParameter("id"));
        String alias = request.getParameter("value");
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        UserAccount account = infusionsoftDataService.findUserAccount(user, accountId);

        try {
            account.setAlias(alias);
            hibernateTemplate.update(account);

            response.getWriter().write(alias);
        } catch (Exception e) {
            log.error("failed to update alias for account " + accountId, e);

            response.sendError(500); // TODO
        }

        return null;
    }

    /**
     * Called from AJAX to validate the existing password.
     */
    public ModelAndView verifyExistingPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        String password = request.getParameter("currentPassword");

        if (infusionsoftPasswordService.isPasswordValid(user, password)) {
            response.getWriter().write("OK");
        } else {
            log.info("existing password is incorrect for user " + user.getId());
            response.sendError(401);
        }

        return null;
    }

    public void setInfusionsoftAuthenticationService(InfusionsoftAuthenticationService infusionsoftAuthenticationService) {
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setInfusionsoftMailService(InfusionsoftMailService infusionsoftMailService) {
        this.infusionsoftMailService = infusionsoftMailService;
    }

    public InfusionsoftDataService getInfusionsoftDataService() {
        return infusionsoftDataService;
    }

    public void setInfusionsoftDataService(InfusionsoftDataService infusionsoftDataService) {
        this.infusionsoftDataService = infusionsoftDataService;
    }

    public void setInfusionsoftPasswordService(InfusionsoftPasswordService infusionsoftPasswordService) {
        this.infusionsoftPasswordService = infusionsoftPasswordService;
    }
}
