package org.apereo.cas.infusionsoft.web.controllers;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.domain.SecurityQuestion;
import org.apereo.cas.infusionsoft.domain.SecurityQuestionResponse;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.apereo.cas.infusionsoft.services.*;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller that backs the new user registration and "forgot password" flows.
 */
@Controller
@RequestMapping(value = {"/registration", "/app/registration"})
public class RegistrationController {
    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private AutoLoginService autoLoginService;
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private CasConfigurationProperties casConfigurationProperties;
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;
    private MailService mailService;
    private PasswordService passwordService;
    private SecurityQuestionService securityQuestionService;
    private ServicesManager servicesManager;
    private UserService userService;
    private String defaultRedirectUrl;

    public RegistrationController(AutoLoginService autoLoginService, InfusionsoftAuthenticationService infusionsoftAuthenticationService, CasConfigurationProperties casConfigurationProperties, InfusionsoftConfigurationProperties infusionsoftConfigurationProperties, MailService mailService, PasswordService passwordService, SecurityQuestionService securityQuestionService, ServicesManager servicesManager, UserService userService, String defaultRedirectUrl) {
        this.autoLoginService = autoLoginService;
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
        this.casConfigurationProperties = casConfigurationProperties;
        this.infusionsoftConfigurationProperties = infusionsoftConfigurationProperties;
        this.mailService = mailService;
        this.passwordService = passwordService;
        this.securityQuestionService = securityQuestionService;
        this.servicesManager = servicesManager;
        this.userService = userService;
        this.defaultRedirectUrl = defaultRedirectUrl;
    }

    /**
     * Shows the registration form.
     *
     * @param model            model
     * @param returnUrl        returnUrl
     * @param userToken        userToken
     * @param firstName        firstName
     * @param lastName         lastName
     * @param email            email
     * @param skipWelcomeEmail skipWelcomeEmail
     * @return view
     * @throws IOException e
     */
    @RequestMapping("/createInfusionsoftId")
    public String createInfusionsoftId(Model model, String returnUrl, String userToken, String firstName, String lastName, String email, @RequestParam(defaultValue = "false") boolean skipWelcomeEmail) throws IOException {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(email);

        buildModelForCreateInfusionsoftId(model, returnUrl, userToken, firstName, lastName, email, skipWelcomeEmail, user);
        return "registration/createInfusionsoftId";
    }

    /**
     * Builds the model with attributes that are required for displaying the createInfusionsoftId page.
     */
    private void buildModelForCreateInfusionsoftId(Model model, String returnUrl, String userToken, String firstName, String lastName, String email, boolean skipWelcomeEmail, User user) {
        if (getServiceByUrl(returnUrl) != null) {
            model.addAttribute("returnUrl", returnUrl);
        }

        model.addAttribute("loginUrl", generateLoginUrl(returnUrl, userToken, firstName, lastName, email, skipWelcomeEmail, user, false));
        model.addAttribute("userToken", userToken);
        model.addAttribute("user", user);
        model.addAttribute("securityQuestions", securityQuestionService.fetchAllEnabled());
        model.addAttribute("skipWelcomeEmail", skipWelcomeEmail);
    }

    /**
     * Determines if the given URL is allowed.  This ensures that we don't have unvalidated redirects.
     */
    private RegisteredService getServiceByUrl(String returnUrl) {
        // Validate the return URL against the service whitelist
        RegisteredService service = null;
        if (StringUtils.isNotBlank(returnUrl)) {
            service = servicesManager.findServiceBy(returnUrl);
            if (service != null) {
                log.debug("returnUrl matched registered service " + service.getName() + ": " + returnUrl);
            } else {
                log.info("returnUrl did not match any active registered service (it will be ignored): " + returnUrl);
            }
        }
        return service;
    }

    /**
     * Creates a link to the login page with the right parameters to support linking and registration
     */
    private String generateLoginUrl(String returnUrl, String userToken, String firstName, String lastName, String email, boolean skipWelcomeEmail, User user, boolean isNewInfusionsoftId) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(casConfigurationProperties.getServer().getLoginUrl());
        uriBuilder.queryParam("service", generateAppLinkageUrl(returnUrl, userToken, user, isNewInfusionsoftId));
        if (!isNewInfusionsoftId) {
            uriBuilder.queryParam("renew", true); // Note: this bypasses SSO, forcing a login
            uriBuilder.queryParam("registration", generateRegistrationUrl(returnUrl, userToken, firstName, lastName, email, skipWelcomeEmail));
        }
        return uriBuilder.toUriString();
    }

    /**
     * Creates a link to the registration page with all supported parameters. They should correspond with {@link #createInfusionsoftId}
     */
    private String generateRegistrationUrl(String returnUrl, String userToken, String firstName, String lastName, String email, boolean skipWelcomeEmail) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(casConfigurationProperties.getServer().getPrefix());
        uriBuilder.path("/registration/createInfusionsoftId");
        if (StringUtils.isNotBlank(returnUrl)) {
            uriBuilder.queryParam("returnUrl", returnUrl);
        }
        if (StringUtils.isNotBlank(userToken)) {
            uriBuilder.queryParam("userToken", userToken);
        }
        if (StringUtils.isNotBlank(firstName)) {
            uriBuilder.queryParam("firstName", firstName);
        }
        if (StringUtils.isNotBlank(lastName)) {
            uriBuilder.queryParam("lastName", lastName);
        }
        if (StringUtils.isNotBlank(email)) {
            uriBuilder.queryParam("email", email);
        }
        if (skipWelcomeEmail) {
            uriBuilder.queryParam("skipWelcomeEmail", skipWelcomeEmail);
        }
        return uriBuilder.toUriString();
    }

    /**
     * Creates the redirect from the return URL, user token, and user.  The page at this URL is responsible for completing the linkage of the CAS ID to an application user and displaying a success page if desired.
     */
    private String generateAppLinkageUrl(String returnUrl, String userToken, User user, boolean isNewInfusionsoftId) {
        // This is used by the login page to redirect back to the app, which will do the linkage
        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = defaultRedirectUrl;
        }
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(returnUrl);
        if (StringUtils.isNotBlank(userToken)) {
            uriBuilder.replaceQueryParam("userToken", userToken);
            uriBuilder.replaceQueryParam("isNewInfusionsoftId", Boolean.toString(isNewInfusionsoftId));
            if (user != null && user.getId() != null) {
                uriBuilder.replaceQueryParam("globalUserId", user.getId());
            }
        }
        return uriBuilder.toUriString();
    }

    /**
     * Registers a new user account.
     *
     * @param model                  model
     * @param firstName              firstName
     * @param lastName               lastName
     * @param username               username
     * @param password               password
     * @param eula                   eula
     * @param returnUrl              returnUrl
     * @param userToken              userToken
     * @param skipWelcomeEmail       skipWelcomeEmail
     * @param securityQuestionId     securityQuestionId
     * @param securityQuestionAnswer securityQuestionAnswer
     * @param request                request
     * @param response               response
     * @return view
     */
    @RequestMapping("/register")
    public String register(Model model, String firstName, String lastName, String username, String password, String eula, String returnUrl, String userToken, @RequestParam(defaultValue = "false") boolean skipWelcomeEmail, Long securityQuestionId, String securityQuestionAnswer, HttpServletRequest request, HttpServletResponse response) {
        boolean eulaChecked = StringUtils.equals(eula, "agreed");
        User user = new User();
        final RegisteredService registeredService = getServiceByUrl(returnUrl);

        try {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEnabled(true);

            model.addAttribute("user", user);

            if (StringUtils.isEmpty(password)) {
                model.addAttribute("error", "password.error.blank");
            } else if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.addAttribute("error", "user.error.email.invalid");
            } else if (StringUtils.isEmpty(firstName)) {
                model.addAttribute("error", "user.error.firstName.blank");
            } else if (StringUtils.isEmpty(lastName)) {
                model.addAttribute("error", "user.error.lastName.blank");
            } else if (userService.isDuplicateUsername(user)) {
                model.addAttribute("error", "user.error.email.inUse.with.link");
            } else if (!eulaChecked) {
                model.addAttribute("error", "registration.error.eula");
            } else if (StringUtils.isBlank(securityQuestionAnswer)) {
                model.addAttribute("error", "registration.error.security.question.answer");
            } else if (securityQuestionId == null) {
                model.addAttribute("error", "registration.error.security.question");
            } else {
                String passwordError = passwordService.validatePassword(user, password);
                if (passwordError != null) {
                    model.addAttribute("error", passwordError);
                }
            }

            if (model.containsAttribute("error")) {
                log.warn("couldn't create new user account: " + model.asMap().get("error"));
            } else {

                user.setIpAddress(request.getRemoteAddr());
                user = userService.createUser(user, password);

                SecurityQuestion securityQuestion = securityQuestionService.fetch(securityQuestionId);
                SecurityQuestionResponse securityQuestionResponse = new SecurityQuestionResponse();
                securityQuestionResponse.setUser(user);
                securityQuestionResponse.setSecurityQuestion(securityQuestion);
                securityQuestionResponse.setResponse(securityQuestionAnswer);
                securityQuestionService.save(securityQuestionResponse);

                user.getSecurityQuestionResponses().add(securityQuestionResponse);

                model.addAttribute("user", user);

                if (!skipWelcomeEmail) {
                    mailService.sendWelcomeEmail(user, request.getLocale());
                }

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
            buildModelForCreateInfusionsoftId(model, returnUrl, userToken, firstName, lastName, username, skipWelcomeEmail, user);
            return "registration/createInfusionsoftId";
        } else if (registeredService != null && StringUtils.isNotBlank(returnUrl)) {
            String loginUrl = generateLoginUrl(returnUrl, userToken, null, null, null, true, user, true);
            log.debug("Registration complete for new user " + user.getUsername() + ". Redirecting to " + loginUrl);
            return "redirect:" + loginUrl;
        } else {
            model.addAttribute("redirectUrl", defaultRedirectUrl);
            return "registration/success";
        }
    }

    /**
     * Shows the "forgot password" dialog.
     *
     * @param model    model
     * @param username username
     * @return view
     */
    @RequestMapping("/forgot")
    public String forgot(Model model, @RequestParam(required = false) String username) {
        model.addAttribute("username", username);
        model.addAttribute("supportPhoneNumbers", infusionsoftConfigurationProperties.getSupportPhoneNumbers());
        return "registration/forgot";
    }

    /**
     * If a valid recovery code is supplied, render the password reset form so they can enter a new
     * password. If not, make them try again.
     *
     * @param model        model
     * @param username     username
     * @param recoveryCode recoveryCode
     * @return view
     */
    @RequestMapping("/recover")
    public String recover(Model model, String username, String recoveryCode) {
        log.info("password recovery request for email " + username);
        model.addAttribute("username", username);
        model.addAttribute("supportPhoneNumbers", infusionsoftConfigurationProperties.getSupportPhoneNumbers());
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
     * Resets the user's password and clears the password recovery code, if the recovery code is valid and the new password meets the rules.
     *
     * @param model        model
     * @param recoveryCode recoveryCode
     * @param password1    password1
     * @param password2    password2
     * @param request      request
     * @param response     response
     * @return view
     */
    @RequestMapping("/reset")
    public String reset(Model model, String recoveryCode, String password1, String password2, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("supportPhoneNumbers", infusionsoftConfigurationProperties.getSupportPhoneNumbers());

        User user = userService.findUserByRecoveryCode(recoveryCode);

        if (user == null) {
            model.addAttribute("error", "forgotpassword.noSuchCode");
        } else if (StringUtils.isEmpty(password1)) {
            model.addAttribute("error", "password.error.blank");
        } else if (!password1.equals(password2)) {
            model.addAttribute("error", "password.error.passwords.dont.match");
        } else {
            try {
                passwordService.setPasswordForUser(user, password1);
                infusionsoftAuthenticationService.completePasswordReset(user);
            } catch (InfusionsoftValidationException e) {
                model.addAttribute("error", e.getErrorMessageCode());
            }
        }

        if (model.containsAttribute("error")) {
            model.addAttribute("recoveryCode", recoveryCode);

            return "registration/reset";
        } else {
            if (user != null) {
                autoLoginService.autoLogin(user.getUsername(), request, response);
            }

            return "redirect:" + defaultRedirectUrl;
        }
    }

    @RequestMapping("/checkPasswordForLast4WithRecoveryCode")
    @ResponseBody
    public boolean checkPasswordForLast4WithRecoveryCode(String recoveryCode, String password1) {
        User user = userService.findUserByRecoveryCode(recoveryCode);

        return user != null && StringUtils.isNotBlank(password1) && !passwordService.lastFourPasswordsContains(user, password1);
    }

}
