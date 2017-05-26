package org.apereo.cas.infusionsoft.web.controllers;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.http.Consts;
import org.apache.http.client.utils.URIBuilder;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.domain.SecurityQuestion;
import org.apereo.cas.infusionsoft.domain.SecurityQuestionResponse;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.apereo.cas.infusionsoft.services.*;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.ServiceTicket;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * Controller that backs the new user registration and "forgot password" flows.
 */
@Controller
@RequestMapping(value = {"/registration", "/app/registration"})
public class RegistrationController {
    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private AutoLoginService autoLoginService;
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;
    private MailService mailService;
    private PasswordService passwordService;
    private SecurityQuestionService securityQuestionService;
    private ServicesManager servicesManager;
    private UserService userService;
    private String defaultRedirectUrl;

    public RegistrationController(AutoLoginService autoLoginService, InfusionsoftAuthenticationService infusionsoftAuthenticationService, InfusionsoftConfigurationProperties infusionsoftConfigurationProperties, MailService mailService, PasswordService passwordService, SecurityQuestionService securityQuestionService, ServicesManager servicesManager, UserService userService, String defaultRedirectUrl) {
        this.autoLoginService = autoLoginService;
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
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
     * @param request          request
     * @return view
     * @throws IOException e
     */
    @RequestMapping("/createInfusionsoftId")
    public String createInfusionsoftId(Model model, String returnUrl, String userToken, String firstName, String lastName, String email, @RequestParam(defaultValue = "false") boolean skipWelcomeEmail, HttpServletRequest request) throws IOException {

        // If you get here, you should not have a ticket granting cookie in the request. If we don't clear it, we may be linking the wrong user account if user chooses "Already have ID" from the registration page
        autoLoginService.killTGT(request);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(email);

        buildModelForCreateInfusionsoftId(model, returnUrl, userToken, user, skipWelcomeEmail);
        return "registration/createInfusionsoftId";
    }

    private String getFullRequestUrl(HttpServletRequest request) {
        // Force SSL (this is needed when running behind the F5, which hides the fact that we're using SSL)
        return ServletUriComponentsBuilder.fromRequest(request).scheme("https").build().toUriString();
    }

    /**
     * Builds the model with attributes that are required for displaying the createInfusionsoftId page.
     */
    private void buildModelForCreateInfusionsoftId(Model model, String returnUrl, String userToken, User user, boolean skipWelcomeEmail) {
        if (getServiceByUrl(returnUrl) != null) {
            model.addAttribute("returnUrl", returnUrl);
        }
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
     * Requires a login in order to identify the user, then redirects a user to the app to finish linking an account.
     *
     * @param returnUrl        returnUrl
     * @param userToken        userToken
     * @return view
     * @throws UnsupportedEncodingException ue
     */
    @RequestMapping("/linkToExisting")
    public String linkToExisting(String returnUrl, String userToken, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String retVal;

        final TicketGrantingTicket ticketGrantingTicket = autoLoginService.getValidTGTFromRequest(request);
        final Authentication authentication = ticketGrantingTicket == null ? null : ticketGrantingTicket.getAuthentication();
        final Principal principal = authentication == null ? null : authentication.getPrincipal();
        User user = null;
        if (principal != null) {
            final long userId = (Long) principal.getAttributes().get("id");
            user = userService.loadUser(userId);
        }
        if (user == null) {
            // Redirect to the login page with this url as the service parameter
            retVal = "redirect:/login?service=" + URLEncoder.encode(request.getRequestURI(), "UTF-8");
        } else {
            final RegisteredService service = getServiceByUrl(returnUrl);
            boolean userTokenIsValid = StringUtils.isNotBlank(userToken);

            if (service != null && userTokenIsValid) {
                final ServiceTicket serviceTicket = autoLoginService.autoLogin(returnUrl, ticketGrantingTicket, request, response);
                final String redirectToAppUrl = generateRedirectToAppFromReturnUrl(serviceTicket, returnUrl, userToken, user, false);
                log.debug("Account linkage request for existing user " + user.getUsername() + ". Redirecting to " + redirectToAppUrl);
                retVal = "redirect:" + redirectToAppUrl;
            } else if (service == null) {
                log.warn("Invalid account linkage request for existing user " + user.getUsername() + " (URL not allowed). Redirecting to app central.");
                retVal = "redirect:/app/central/home";
            } else {
                log.warn("Invalid account linkage request for existing user " + user.getUsername() + " (user token missing). Redirecting to app central.");
                retVal = "redirect:/app/central/home";
            }
        }

        return retVal;
    }

    /**
     * Creates the redirect from the return URL, user token, and user.  The page at this URL is responsible for completing the linkage of the CAS ID to an application user and displaying a success page if desired.
     */
    private String generateRedirectToAppFromReturnUrl(ServiceTicket serviceTicket, String returnUrl, String userToken, User user, boolean isNewInfusionsoftId) {
        // Redirect back to the app, which will do the linkage
        try {
            URIBuilder uriBuilder = new URIBuilder(returnUrl);
            uriBuilder.setCharset(Consts.UTF_8);
            if (StringUtils.equals(userToken, "ticket")) {
                uriBuilder.addParameter("ticket", serviceTicket.getId());
            } else {
                uriBuilder.addParameter("userToken", serviceTicket.getId());
                uriBuilder.addParameter("globalUserId", Objects.toString(user.getId(), null));
                uriBuilder.addParameter("isNewInfusionsoftId", Boolean.toString(isNewInfusionsoftId));
            }
            return uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Bad returnUrl format", e);
        }
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
        ServiceTicket serviceTicket = null;
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

                serviceTicket = autoLoginService.autoLogin(returnUrl, user.getUsername(), request, response);
                if (serviceTicket == null) {
                    model.addAttribute("error", "registration.error.exception");
                }
            }
        } catch (InfusionsoftValidationException e) {
            log.error("failed to create user account", e);
            model.addAttribute("error", e.getErrorMessageCode());
        } catch (Exception e) {
            log.error("failed to create user account", e);
            model.addAttribute("error", "registration.error.exception");
        }

        if (model.containsAttribute("error")) {
            buildModelForCreateInfusionsoftId(model, returnUrl, userToken, user, skipWelcomeEmail);
            return "registration/createInfusionsoftId";
        } else if (registeredService != null && StringUtils.isNotBlank(userToken)) {
            String redirectToAppUrl = generateRedirectToAppFromReturnUrl(serviceTicket, returnUrl, userToken, user, true);
            log.info("Registration complete for new user " + user.getUsername() + ". Redirecting to " + redirectToAppUrl);
            return "redirect:" + redirectToAppUrl;
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
