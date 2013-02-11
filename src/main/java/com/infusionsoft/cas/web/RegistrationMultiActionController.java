package com.infusionsoft.cas.web;

import com.infusionsoft.cas.services.*;
import com.infusionsoft.cas.types.AppType;
import com.infusionsoft.cas.types.PendingUserAccount;
import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller that backs the new user registration and "forgot password" flows.
 */
public class RegistrationMultiActionController extends MultiActionController {
    private static final Logger log = Logger.getLogger(RegistrationMultiActionController.class);

    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private InfusionsoftDataService infusionsoftDataService;
    private CustomerHubService customerHubService;
    private PasswordService passwordService;
    private MailService mailService;
    private HibernateTemplate hibernateTemplate;
    private UniqueTicketIdGenerator ticketIdGenerator;

    /**
     * Shows the registration form.
     */
    public ModelAndView welcome(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        String registrationCode = (String) request.getSession(true).getAttribute("registrationCode");
        User user = new User();

        // TODO - expire any existing CAS session and log them out to prevent confusion

        if (StringUtils.isNotEmpty(registrationCode)) {
            PendingUserAccount pending = infusionsoftDataService.findPendingUserAccount(registrationCode);

            if (pending != null) {
                user.setFirstName(pending.getFirstName());
                user.setLastName(pending.getLastName());
                user.setUsername(pending.getEmail());
            }
        }

        model.put("loginTicket", ticketIdGenerator.getNewTicketId("LT"));
        model.put("user", user);

        return new ModelAndView("infusionsoft/ui/registration/welcome", model);
    }

    /**
     * Shows the registration form.
     */
    public ModelAndView banner(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();

        return new ModelAndView("infusionsoft/ui/registration/banner", model);
    }

    /**
     * Registers a new user account.
     */
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String username = request.getParameter("username");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        boolean eula = StringUtils.equals(request.getParameter("eula"), "agreed");
        String registrationCode = (String) request.getSession(true).getAttribute("registrationCode");
        Map<String, Object> model = new HashMap<String, Object>();
        User user;

        try {
            user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEnabled(true);

            model.put("user", user);

            if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.put("error", "registration.error.invalidUsername");
            } else if (hibernateTemplate.find("from User u where u.username = ?", username).size() > 0) {
                model.put("error", "registration.error.usernameInUse");
            } else if (StringUtils.isEmpty(password1) || StringUtils.isEmpty(password2)) {
                model.put("error", "registration.error.invalidPassword");
            } else if (!password1.equals(password2)) {
                model.put("error", "registration.error.passwordsNoMatch");
            } else if (!eula) {
                model.put("error", "registration.error.eula");
            } else {
                String passwordError = passwordService.validatePassword(user, username, password1);

                if (passwordError != null) {
                    model.put("error", passwordError);
                }
            }

            if (model.containsKey("error")) {
                log.warn("couldn't create new user account: " + model.get("error"));
            } else {
                hibernateTemplate.save(user);

                passwordService.setPasswordForUser(user, password1);
                infusionsoftAuthenticationService.createTicketGrantingTicket(username, request, response);

                if (StringUtils.isNotEmpty(registrationCode)) {
                    log.info("processing registration code " + registrationCode);

                    try {
                        UserAccount account = infusionsoftDataService.associatePendingAccountToUser(user, registrationCode);

                        request.getSession().removeAttribute("registrationCode");
                        infusionsoftAuthenticationService.createTicketGrantingTicket(username, request, response);
                    } catch (Exception e) {
                        log.error("failed to associate new user to registration code " + registrationCode, e);
                    }
                }

                mailService.sendWelcomeEmail(user);
            }
        } catch (Exception e) {
            log.error("failed to create user account", e);

            model.put("error", "registration.error.exception");
        }

        if (model.containsKey("error")) {
            return new ModelAndView("infusionsoft/ui/registration/welcome", model);
        } else {
            if (StringUtils.isNotEmpty((String) request.getSession(true).getAttribute("serviceUrl"))) {
                try {
                    String serviceUrl = (String) request.getSession(true).getAttribute("serviceUrl");
                    String appName = infusionsoftAuthenticationService.guessAppName(new URL(serviceUrl));
                    String appType = infusionsoftAuthenticationService.guessAppType(new URL(serviceUrl));

                    if (StringUtils.equals(appType, AppType.CRM) && !StringUtils.equals(appType, AppType.CAS)) {
                        return new ModelAndView("redirect:verification");
                    }
                } catch (Exception e) {
                    log.warn("failed to parse appName/appType from serviceUrl", e);
                }
            }

            return new ModelAndView("redirect:success");
        }
    }

    /**
     * If the user came here from an app, verify that they are already associated to it. If so, send them to the
     * success action. If not, show the form to get their credentials.
     */
    public ModelAndView verification(HttpServletRequest request, HttpServletResponse response) {
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        HttpSession session = request.getSession(true);
        String appName = null;
        String appType = null;
        Map<String, Object> model = new HashMap<String, Object>();

        try {
            URL serviceUrl = new URL((String) session.getAttribute("serviceUrl"));

            appName = infusionsoftAuthenticationService.guessAppName(serviceUrl);
            appType = infusionsoftAuthenticationService.guessAppName(serviceUrl);

            model.put("appName", appName);
            model.put("appType", appType);
            model.put("appDomain", appName + "." + infusionsoftAuthenticationService.getCrmDomain());
        } catch (Exception e) {
            log.warn("failed to parse appName/appType from serviceUrl", e);
        }

        if (StringUtils.isNotEmpty(appName) && StringUtils.isNotEmpty(appType)) {
            if (infusionsoftAuthenticationService.isUserAssociated(user, appType, appName)) {
                return new ModelAndView("redirect:success");
            } else {
                return new ModelAndView("infusionsoft/ui/registration/verification", model);
            }
        } else {
            return new ModelAndView("redirect:success");
        }
    }

    public ModelAndView verify(HttpServletRequest request, HttpServletResponse response) {
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        HttpSession session = request.getSession(true);
        String appName = null;
        String appType = null;

        try {
            URL serviceUrl = new URL((String) session.getAttribute("serviceUrl"));

            appName = infusionsoftAuthenticationService.guessAppName(serviceUrl);
            appType = infusionsoftAuthenticationService.guessAppType(serviceUrl);
        } catch (Exception e) {
            log.warn("failed to parse appName/appType from serviceUrl", e);
        }

        String appUsername = request.getParameter("appUsername");
        String appPassword = request.getParameter("appPassword");
        Map<String, Object> model = new HashMap<String, Object>();

        if (infusionsoftAuthenticationService.verifyAppCredentials(appType, appName, appUsername, appPassword)) {
            try {
                UserAccount account = infusionsoftDataService.associateAccountToUser(user, appType, appName, appUsername);

                infusionsoftAuthenticationService.createTicketGrantingTicket(user.getUsername(), request, response);

                return new ModelAndView("redirect:success", "appUrl", infusionsoftAuthenticationService.buildAppUrl(account.getAppType(), account.getAppName()));
            } catch (Exception e) {
                log.error("failed to associate verified credentials", e);
            }
        }

        model.put("appUsername", appUsername);
        model.put("error", "registration.error.invalidLegacyCredentials");

        return new ModelAndView("infusionsoft/ui/registration/verification", model);
    }

    /**
     * Shows the registration success page.
     */
    public ModelAndView success(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = infusionsoftAuthenticationService.getCurrentUser(request);

        if (user == null) {
            return new ModelAndView("redirect:welcome");
        } else {
            model.put("user", user);

            if (user.getAccounts().size() == 1) {
                UserAccount primary = new ArrayList<UserAccount>(user.getAccounts()).get(0);

                model.put("appUrl", infusionsoftAuthenticationService.buildAppUrl(primary.getAppType(), primary.getAppName()));
            }

            return new ModelAndView("infusionsoft/ui/registration/success", model);
        }
    }

    /**
     * Shows the "forgot password" dialog.
     */
    public ModelAndView forgot(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("infusionsoft/ui/registration/forgot");
    }

    /**
     * If a valid recovery code is supplied, render the password reset form so they can enter a new
     * password. If not, make them try again.
     */
    public ModelAndView recover(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter("username");
        String recoveryCode = request.getParameter("recoveryCode");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");

        log.info("password recovery request for email " + email);

        if (StringUtils.isNotEmpty(recoveryCode)) {
            User user = infusionsoftDataService.findUserByRecoveryCode(recoveryCode);

            if (user == null) {
                log.warn("invalid password recovery code was entered: " + recoveryCode);

                return new ModelAndView("infusionsoft/ui/registration/recover", "error", "forgotpassword.noSuchCode");
            } else {
                log.info("correct password recovery code was entered for user " + user.getId());

                return new ModelAndView("infusionsoft/ui/registration/reset", "recoveryCode", recoveryCode);
            }
        } else if (StringUtils.isNotEmpty(email)) {
            List<User> users = (List<User>) hibernateTemplate.find("from User where username = ?", email);

            if (users.size() > 0) {
                recoveryCode = infusionsoftDataService.createPasswordRecoveryCode(users.get(0));

                log.info("password recovery code " + recoveryCode + " created for user " + users.get(0).getId());

                mailService.sendPasswordResetEmail(users.get(0));

                return new ModelAndView("infusionsoft/ui/registration/recover", "recoveryCode", recoveryCode);
            } else {
                log.warn("password recovery attempted for non-existent user: " + email);

                return new ModelAndView("infusionsoft/ui/registration/forgot", "error", "forgotpassword.noSuchUser");
            }
        } else {
            return new ModelAndView("infusionsoft/ui/registration/forgot");
        }
    }

    /**
     * Resets the user's password, if the recovery code is valid and the new password meets the rules.
     */
    public ModelAndView reset(HttpServletRequest request, HttpServletResponse response) {
        String recoveryCode = request.getParameter("recoveryCode");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        Map<String, Object> model = new HashMap<String, Object>();
        User user = infusionsoftDataService.findUserByRecoveryCode(recoveryCode);

        if (user == null) {
            model.put("error", "forgotpassword.noSuchCode");
        } else if (StringUtils.isEmpty(password1) || StringUtils.isEmpty(password2)) {
            model.put("error", "registration.error.invalidPassword");
        } else if (!password1.equals(password2)) {
            model.put("error", "registration.error.passwordsNoMatch");
        } else {
            String passwordError = passwordService.validatePassword(user, user.getUsername(), password1);

            if (passwordError != null) {
                model.put("error", passwordError);
            }
        }

        if (model.containsKey("error")) {
            model.put("recoveryCode", recoveryCode);

            return new ModelAndView("infusionsoft/ui/registration/reset", model);
        } else {
            passwordService.setPasswordForUser(user, password1);

            return new ModelAndView("redirect:/login");
        }
    }

    /**
     * Called from AJAX to get a URL to an app logo, if available.
     */
    public ModelAndView getLogoImageUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String appType = request.getParameter("appType");
        String appName = request.getParameter("appName");
        String url = "";

        try {
            if (StringUtils.isNotEmpty(appType) && StringUtils.isNotEmpty(appName)) {
                if (appType.equals(AppType.CRM)) {
                    url = infusionsoftAuthenticationService.buildAppUrl(appType, appName) + "/Logo?logo=weblogo";
                } else {
                    url = customerHubService.getLogoUrl(appName);
                }
            }
        } catch (Exception e) {
            log.error("unable to get app url for " + appName + "/" + appType);
        }

        if (StringUtils.isNotEmpty(url)) {
            log.debug("returning app logo url " + url + " for " + appName + "/" + appType);
        } else {
            log.debug("app logo url is unavailable for " + appName + "/" + appType);
        }

        response.getWriter().write(url);

        return null;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setTicketIdGenerator(UniqueTicketIdGenerator ticketIdGenerator) {
        this.ticketIdGenerator = ticketIdGenerator;
    }

    public void setInfusionsoftAuthenticationService(InfusionsoftAuthenticationService infusionsoftAuthenticationService) {
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setInfusionsoftDataService(InfusionsoftDataService infusionsoftDataService) {
        this.infusionsoftDataService = infusionsoftDataService;
    }

    public void setPasswordService(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    public void setCustomerHubService(CustomerHubService customerHubService) {
        this.customerHubService = customerHubService;
    }
}
