package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.PendingUserAccount;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.AccountException;
import com.infusionsoft.cas.exceptions.InfusionsoftValidationException;
import com.infusionsoft.cas.services.*;
import com.infusionsoft.cas.support.AppHelper;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller that backs the new user registration and "forgot password" flows.
 */
@Controller
@RequestMapping(value = {"/registration", "/app/registration"})
public class RegistrationController {
    private static final Logger log = Logger.getLogger(RegistrationController.class);

    @Autowired
    private AppHelper appHelper;

    @Autowired
    private CustomerHubService customerHubService;

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UrlService urlService;

    @Autowired
    private UserService userService;

    @Autowired
    private AutoLoginService autoLoginService;


    @Value("${server.prefix}")
    private String serverPrefix;

    /**
     * Uses the new action for now.  Once old registrations finish we can kill this action
     */
    //TODO: Kill after a couple weeks after any outstanding new user invites have processed.
    @RequestMapping
    public ModelAndView welcome(String registrationCode, String returnUrl, String skipUrl, String userToken, String firstName, String lastName, String email, HttpServletRequest request) throws IOException {
        return createInfusionsoftId(registrationCode, returnUrl, skipUrl, userToken, firstName, lastName, email, request);
    }

    /**
     * Shows the registration form.
     */
    @RequestMapping
    public ModelAndView createInfusionsoftId(String registrationCode, String returnUrl, String skipUrl, String userToken, String firstName, String lastName, String email, HttpServletRequest request) throws IOException {
        //If you get here, you should not have a ticket granting cookie in the request. If we don't clear it, we may be linking the wrong user account if user chooses "Already have ID" from the registration page
        autoLoginService.killTGT(request);

        Map<String, Object> model = new HashMap<String, Object>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String service = serverPrefix + urlService.url("registration", "createInfusionsoftId") + (StringUtils.isNotEmpty(registrationCode) ? "?registrationCode=" + registrationCode : "");
            return new ModelAndView("redirect://j_spring_security_logout?service=" + URLEncoder.encode(service, "UTF-8"));
        } else {
            User user = new User();
            user.setFirstName(StringEscapeUtils.escapeHtml4(firstName));
            user.setLastName(StringEscapeUtils.escapeHtml4(lastName));
            user.setUsername(StringEscapeUtils.escapeHtml4(email));

            // If there's a registration code, pre-populate from that
            if (StringUtils.isNotEmpty(registrationCode)) {
                PendingUserAccount pending = userService.findPendingUserAccount(registrationCode);

                if (pending != null) {
                    user.setFirstName(StringEscapeUtils.escapeHtml4(pending.getFirstName()));
                    user.setLastName(StringEscapeUtils.escapeHtml4(pending.getLastName()));
                    user.setUsername(StringEscapeUtils.escapeHtml4(pending.getEmail()));
                }
            }

            model.put("returnUrl", returnUrl);
            model.put("skipUrl", skipUrl);
            model.put("userToken", userToken);
            model.put("user", user);
            model.put("registrationCode", registrationCode);

            return new ModelAndView("registration/createInfusionsoftId", model);
        }
    }

    /**
     * Either connects a pending CAS account with a real CAS account, or redirects a user to the app to finish linking an account.
     */
    @RequestMapping
    public String linkToExisting(Model model, String registrationCode, String returnUrl, String userToken, HttpServletRequest request, HttpServletResponse response) throws AccountException, UnsupportedEncodingException {
        String retVal;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (StringUtils.isNotEmpty(registrationCode)) {
            PendingUserAccount pending = userService.findPendingUserAccount(registrationCode);

            if (pending != null) {
                userService.associatePendingAccountToUser(user, registrationCode);
                autoLoginService.autoLogin(user.getUsername(), request, response);

                retVal = "redirect:/app/central/home";
            } else {
                model.addAttribute("error", "Registration Code not found");
                retVal = "registration/createInfusionsoftId";
            }
        } else if (StringUtils.isNotBlank(returnUrl) && StringUtils.isNotBlank(userToken)) {
            // Redirect back to the app, which will do the linkage
            // TODO: we need to sanitize / validate the returnUrl!
            // TODO: is this suffficient for userToken?
            return "redirect:" + returnUrl + "?userToken=" + URLEncoder.encode(userToken, CharEncoding.UTF_8) + "&casGlobalId=" + user.getId();
        } else {
            return "redirect:/app/central/home";
        }

        return retVal;
    }

    /**
     * Registers a new user account.
     */
    @RequestMapping
    public String register(Model model, String firstName, String lastName, String username, String username2, String password1, String password2, String eula, String registrationCode, String returnUrl, String skipUrl, String userToken, HttpServletRequest request, HttpServletResponse response) {
        boolean eulaChecked = StringUtils.equals(eula, "agreed");
        User user = new User();

        try {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEnabled(true);

            model.addAttribute("user", user);

            if (StringUtils.isEmpty(password1)) {
                model.addAttribute("error", "password.error.blank");
            } else if (!password1.equals(password2)) {
                model.addAttribute("error", "password.error.passwords.dont.match");
            } else if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.addAttribute("error", "user.error.email.invalid");
            } else if (!username.equals(username2)) {
                model.addAttribute("error", "user.error.emails.dont.match");
            } else if (StringUtils.isEmpty(firstName)) {
                model.addAttribute("error", "user.error.firstName.blank");
            } else if (StringUtils.isEmpty(lastName)) {
                model.addAttribute("error", "user.error.lastName.blank");
            } else if (userService.isDuplicateUsername(user)) {
                model.addAttribute("error", "user.error.email.inUse.with.link");
            } else if (!eulaChecked) {
                model.addAttribute("error", "registration.error.eula");
            } else {
                String passwordError = passwordService.validatePassword(user, password1);
                if (passwordError != null) {
                    model.addAttribute("error", passwordError);
                }
            }

            if (model.containsAttribute("error")) {
                log.warn("couldn't create new user account: " + model.asMap().get("error"));
            } else {

                user = userService.saveUser(user, password1);
                model.addAttribute("user", user);

                if (StringUtils.isNotEmpty(registrationCode)) {
                    log.info("processing registration code " + registrationCode);

                    try {
                        userService.associatePendingAccountToUser(user, registrationCode);
                    } catch (Exception e) {
                        log.error("failed to associate new user to registration code " + registrationCode, e);
                    }
                }

                mailService.sendWelcomeEmail(user);
                autoLoginService.autoLogin(user.getUsername(), request, response);
            }
        } catch (InfusionsoftValidationException e) {
            log.error("failed to create user account", e);
            model.addAttribute("error", e.getErrorMessageCode());
        } catch (Exception e) {
            log.error("failed to create user account", e);
            model.addAttribute("error", "registration.error.exception");
        }

        if (model.containsAttribute("error")) {
            model.addAttribute("returnUrl", returnUrl);
            model.addAttribute("skipUrl", skipUrl);
            model.addAttribute("userToken", userToken);
            model.addAttribute("registrationCode", registrationCode);
            return "registration/createInfusionsoftId";
        } else {
            if (StringUtils.isNotBlank(userToken) && StringUtils.isNotBlank(returnUrl)) {
                return "redirect:" + returnUrl + "?userToken=" + userToken + "&casGlobalId=" + user.getId();
            } else {
                return "registration/success";
            }
        }
    }

    /**
     * Shows the registration success page.
     */
    @RequestMapping
    public ModelAndView success(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = infusionsoftAuthenticationService.getCurrentUser(request);

        if (user == null) {
            return new ModelAndView("redirect:createInfusionsoftId");
        } else {
            model.put("user", user);

            if (user.getAccounts().size() == 1) {
                UserAccount primary = new ArrayList<UserAccount>(user.getAccounts()).get(0);

                model.put("appUrl", appHelper.buildAppUrl(primary.getAppType(), primary.getAppName()));
            }

            return new ModelAndView("registration/success", model);
        }
    }

    /**
     * Shows the "forgot password" dialog.
     */
    @RequestMapping
    public String forgot(Model model, @RequestParam(required = false) String username) {
        model.addAttribute("username", username);
        return "registration/forgot";
    }

    /**
     * If a valid recovery code is supplied, render the password reset form so they can enter a new
     * password. If not, make them try again.
     */
    @RequestMapping
    public String recover(Model model, String username, String recoveryCode) {
        log.info("password recovery request for email " + username);
        model.addAttribute("username", username);
        recoveryCode = StringUtils.trim(recoveryCode);

        if (StringUtils.isNotEmpty(recoveryCode)) {
            //Checking provided recovery code
            User user = userService.findUserByRecoveryCode(recoveryCode);

            if (user == null) {
                log.warn("invalid password recovery code was entered: " + recoveryCode);
                model.addAttribute("error", "forgotpassword.noSuchCode");
                return "registration/recover";
            } else {
                log.info("correct password recovery code was entered for user " + user.getId());
                model.addAttribute("recoveryCode", recoveryCode);
                return "registration/reset";
            }
        } else if (StringUtils.isNotEmpty(username)) {
            //Recovery Code Requested
            User user = userService.findEnabledUser(username);

            if (user != null) {
                userService.resetPassword(user);
                log.info("password recovery code created for user " + user.getId());
            } else {
                log.warn("password recovery attempted for non-existent user: " + username);
                model.addAttribute("error", "forgotpassword.noSuchUser");
                return "registration/forgot";
            }

            return "registration/recover";
        } else {
            //Not requesting new code nor provided existing code
            return "registration/forgot";
        }
    }

    /**
     * Resets the user's password, if the recovery code is valid and the new password meets the rules.
     */
    @RequestMapping
    public ModelAndView reset(String recoveryCode, String password1, String password2, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = userService.findUserByRecoveryCode(recoveryCode);

        if (user == null) {
            model.put("error", "forgotpassword.noSuchCode");
        } else if (StringUtils.isEmpty(password1)) {
            model.put("error", "password.error.blank");
        } else if (!password1.equals(password2)) {
            model.put("error", "password.error.passwords.dont.match");
        } else {
            try {
                passwordService.setPasswordForUser(user, password1);
            } catch (InfusionsoftValidationException e) {
                model.put("error", e.getErrorMessageCode());
            }
        }

        if (model.containsKey("error")) {
            model.put("recoveryCode", recoveryCode);

            return new ModelAndView("registration/reset", model);
        } else {
            if (user != null) {
                autoLoginService.autoLogin(user.getUsername(), request, response);
            }

            return new ModelAndView("redirect:/app/central/home");
        }
    }

    /**
     * Called from AJAX to get a URL to an app logo, if available.
     */
    @RequestMapping
    @ResponseBody
    public String getLogoImageUrl(AppType appType, String appName) throws IOException {
        String url = "";

        try {
            if (appType != null && StringUtils.isNotEmpty(appName)) {
                if (appType.equals(AppType.CRM)) {
                    url = appHelper.buildAppUrl(appType, appName) + "/Logo?logo=weblogo";
                } else if (appType.equals(AppType.CUSTOMERHUB)) {
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


        return url;
    }
}
