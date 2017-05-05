package org.apereo.cas.infusionsoft.web.controllers;

import org.apereo.cas.infusionsoft.authentication.LoginResult;
import org.apereo.cas.infusionsoft.domain.*;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.apereo.cas.infusionsoft.services.*;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.jasig.cas.services.RegisteredService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
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
    private CrmService crmService;

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private MailService mailService;

    @Autowired
    SecurityQuestionService securityQuestionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AutoLoginService autoLoginService;

    @Autowired
    private CasRegisteredServiceService registeredServiceService;

    @Autowired
    private SupportContactService supportContactService;

    @Value("${cas.viewResolver.basename}")
    private String viewResolverBaseName;

    private String getViewBase() {
        return "default_views".equals(viewResolverBaseName) ? "" : viewResolverBaseName + "/";
    }

    @PostConstruct
    public void init() {

    }

    /**
     * Uses the new action for now.  Once old registrations finish we can kill this action
     *
     * @param model            model
     * @param registrationCode registrationCode
     * @param returnUrl        returnUrl
     * @param skipUrl          skipUrl
     * @param userToken        userToken
     * @param firstName        firstName
     * @param lastName         lastName
     * @param email            email
     * @param request          request
     * @return view
     * @throws IOException e
     */
    //TODO: Kill after a couple weeks after any outstanding new user invites have processed.
    // NOTE: as of March 2014 this is still being hit (828 times in the last month)!  It is showing up in Google search
    // results, and people also must have it bookmarked.  How do we kill this without impacting customers?
    @RequestMapping
    public String welcome(Model model, String registrationCode, String returnUrl, String skipUrl, String userToken, String firstName, String lastName, String email, HttpServletRequest request) throws IOException {
        return createInfusionsoftId(model, registrationCode, returnUrl, skipUrl, userToken, firstName, lastName, email, true, request);
    }

    /**
     * Shows the registration form.
     *
     * @param model            model
     * @param registrationCode registrationCode
     * @param returnUrl        returnUrl
     * @param skipUrl          skipUrl
     * @param userToken        userToken
     * @param firstName        firstName
     * @param lastName         lastName
     * @param email            email
     * @param skipWelcomeEmail skipWelcomeEmail
     * @param request          request
     * @return view
     * @throws IOException e
     */
    @RequestMapping
    public String createInfusionsoftId(Model model, String registrationCode, String returnUrl, String skipUrl, String userToken, String firstName, String lastName, String email, @RequestParam(defaultValue = "false") boolean skipWelcomeEmail, HttpServletRequest request) throws IOException {
//        if(true) {
//            throw new IOException();
//        }

        // If you get here, you should not have a ticket granting cookie in the request. If we don't clear it, we may be linking the wrong user account if user chooses "Already have ID" from the registration page
        autoLoginService.killTGT(request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            log.debug("User is registering while already logged in as " + authentication.getName() + "; redirecting to j_spring_security_logout");
            return "redirect:/j_spring_security_logout?service=" + URLEncoder.encode(getFullRequestUrl(request), "UTF-8");
        } else {
            //NOTE: these values get escaped by the <form:input htmlEscape=true>
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(email);

            // TODO: upgrade: remove support for registration codes?
            // If there's a registration code, pre-populate from that
//            if (StringUtils.isNotEmpty(registrationCode)) {
//                PendingUserAccount pending = userService.findPendingUserAccount(registrationCode);
//
//                if (pending != null) {
//                    //NOTE: these values get escaped by the <form:input htmlEscape=true>
//                    user.setFirstName(pending.getFirstName());
//                    user.setLastName(pending.getLastName());
//                    user.setUsername(pending.getEmail());
//                }
//            }

            buildModelForCreateInfusionsoftId(model, returnUrl, skipUrl, userToken, user, registrationCode, skipWelcomeEmail);
            return "registration/createInfusionsoftId";
        }
    }

    private String getFullRequestUrl(HttpServletRequest request) {
        // Force SSL (this is needed when running behind the F5, which hides the fact that we're using SSL)
        return ServletUriComponentsBuilder.fromRequest(request).scheme("https").build().toUriString();
    }

    /**
     * Builds the model with attributes that are required for displaying the createInfusionsoftId page.
     */
    private void buildModelForCreateInfusionsoftId(Model model, String returnUrl, String skipUrl, String userToken, User user, String registrationCode, boolean skipWelcomeEmail) {
        if (isAllowedUrl(returnUrl, "returnUrl")) {
            model.addAttribute("returnUrl", returnUrl);
        }
        if (isAllowedUrl(skipUrl, "skipUrl")) {
            model.addAttribute("skipUrl", skipUrl);
        }
        model.addAttribute("userToken", userToken);
        model.addAttribute("user", user);
        model.addAttribute("securityQuestions", securityQuestionService.fetchAllEnabled());
        model.addAttribute("registrationCode", registrationCode);
        model.addAttribute("skipWelcomeEmail", skipWelcomeEmail);
    }

    /**
     * Determines if the given URL is allowed.  This ensures that we don't have unvalidated redirects.
     */
    private boolean isAllowedUrl(String url, String parameterName) {
        // Validate the return URL against the service whitelist
        /** Modeled after {@link org.jasig.cas.web.LogoutController#handleRequestInternal(HttpServletRequest, HttpServletResponse)} }*/
        boolean retVal = false;
        final RegisteredService registeredService = registeredServiceService.getEnabledRegisteredServiceByUrl(url);
        if (registeredService != null) {
            retVal = true;
            log.info("URL " + parameterName + " matched registered service " + registeredService.getName() + ": " + url);
        } else {
            log.warn("URL " + parameterName + " did not match any active registered service (it will be ignored): " + url);
        }
        return retVal;
    }

    /**
     * Either connects a pending CAS account with a real CAS account, or redirects a user to the app to finish linking an account.
     *
     * @param model            model
     * @param registrationCode registrationCode
     * @param returnUrl        returnUrl
     * @param userToken        userToken
     * @param request          request
     * @param response         response
     * @return view
     * @throws UnsupportedEncodingException ue
     */
    @RequestMapping
    public String linkToExisting(Model model, String registrationCode, String returnUrl, String userToken, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String retVal;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // TODO: upgrade: remove support for registration codes?
//        if (StringUtils.isNotEmpty(registrationCode)) {
//            PendingUserAccount pending = userService.findPendingUserAccount(registrationCode);
//
//            if (pending != null) {
//                userService.associatePendingAccountToUser(user, registrationCode);
//                autoLoginService.autoLogin(user.getUsername(), request, response);
//
//                retVal = "redirect:/app/central/home";
//            } else {
//                model.addAttribute("error", "Registration Code not found");
//                retVal = "registration/createInfusionsoftId";
//            }
//        } else {
            boolean urlIsAllowed = isAllowedUrl(returnUrl, "returnUrl");
            boolean userTokenIsValid = StringUtils.isNotBlank(userToken);

            if (urlIsAllowed && userTokenIsValid) {
                String redirectToAppUrl = generateRedirectToAppFromReturnUrl(returnUrl, userToken, user, false);
                log.info("Account linkage request for existing user " + user.getUsername() + ". Redirecting to " + redirectToAppUrl);
                retVal = "redirect:" + redirectToAppUrl;
            } else if (!urlIsAllowed) {
                log.warn("Invalid account linkage request for existing user " + user.getUsername() + " (URL not allowed). Redirecting to app central.");
                retVal = "redirect:/app/central/home";
            } else {
                log.warn("Invalid account linkage request for existing user " + user.getUsername() + " (user token missing). Redirecting to app central.");
                retVal = "redirect:/app/central/home";
            }
//        }

        return retVal;
    }

    /**
     * Creates the redirect from the return URL, user token, and user.  The page at this URL is responsible for completing the linkage of the CAS ID to an application user and displaying a success page if desired.
     */
    private String generateRedirectToAppFromReturnUrl(String returnUrl, String userToken, User user, boolean isNewInfusionsoftId) {
        // Redirect back to the app, which will do the linkage
        try {
            return returnUrl + "?userToken=" + URLEncoder.encode(userToken, CharEncoding.UTF_8) + "&casGlobalId=" + user.getId() + "&globalUserId=" + user.getId() + "&isNewInfusionsoftId=" + isNewInfusionsoftId;
            // TODO: change to this once all apps use globalUserId instead of casGlobalId:
            // return returnUrl + "?userToken=" + URLEncoder.encode(userToken, CharEncoding.UTF_8) + "&globalUserId=" + user.getId() + "&isNewInfusionsoftId=" + isNewInfusionsoftId;
        } catch (UnsupportedEncodingException e) {
            // This should never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a new user account.
     *
     * @param model                  model
     * @param firstName              firstName
     * @param lastName               lastName
     * @param username               username
     * @param username2              username2
     * @param password1              password1
     * @param password2              password2
     * @param eula                   eula
     * @param registrationCode       registrationCode
     * @param returnUrl              returnUrl
     * @param skipUrl                skipUrl
     * @param userToken              userToken
     * @param skipWelcomeEmail       skipWelcomeEmail
     * @param securityQuestionId     securityQuestionId
     * @param securityQuestionAnswer securityQuestionAnswer
     * @param request                request
     * @param response               response
     * @return view
     */
    @RequestMapping
    public String register(Model model, String firstName, String lastName, String username, String username2, String password1, String password2, String eula, String registrationCode, String returnUrl, String skipUrl, String userToken, @RequestParam(defaultValue = "false") boolean skipWelcomeEmail, Long securityQuestionId, String securityQuestionAnswer, HttpServletRequest request, HttpServletResponse response) {
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
            } else if (StringUtils.isBlank(securityQuestionAnswer)) {
                model.addAttribute("error", "registration.error.security.question.answer");
            } else if (securityQuestionId == null) {
                model.addAttribute("error", "registration.error.security.question");
            } else {
                String passwordError = passwordService.validatePassword(user, password1);
                if (passwordError != null) {
                    model.addAttribute("error", passwordError);
                }
            }

            if (model.containsAttribute("error")) {
                log.warn("couldn't create new user account: " + model.asMap().get("error"));
            } else {

                user = userService.createUser(user, password1);

                SecurityQuestion securityQuestion = securityQuestionService.fetch(securityQuestionId);
                SecurityQuestionResponse securityQuestionResponse = new SecurityQuestionResponse();
                securityQuestionResponse.setUser(user);
                securityQuestionResponse.setSecurityQuestion(securityQuestion);
                securityQuestionResponse.setResponse(securityQuestionAnswer);
                securityQuestionService.save(securityQuestionResponse);

                user.getSecurityQuestionResponses().add(securityQuestionResponse);

                model.addAttribute("user", user);

                // TODO: upgrade: remove support for registration codes?
//                if (StringUtils.isNotEmpty(registrationCode)) {
//                    log.info("processing registration code " + registrationCode);
//
//                    try {
//                        userService.associatePendingAccountToUser(user, registrationCode);
//                    } catch (Exception e) {
//                        log.error("failed to associate new user to registration code " + registrationCode, e);
//                    }
//                }

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
            buildModelForCreateInfusionsoftId(model, returnUrl, skipUrl, userToken, user, registrationCode, skipWelcomeEmail);
            return "registration/createInfusionsoftId";
        } else if (isAllowedUrl(returnUrl, "returnUrl") && StringUtils.isNotBlank(userToken)) {
            String redirectToAppUrl = generateRedirectToAppFromReturnUrl(returnUrl, userToken, user, true);
            log.info("Registration complete for new user " + user.getUsername() + ". Redirecting to " + redirectToAppUrl);
            return "redirect:" + redirectToAppUrl;
        } else {
            return "registration/success";
        }
    }

    /**
     * Shows the registration success page.
     *
     * @param request request
     * @return ModelAndView
     */
    @RequestMapping
    public ModelAndView success(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<String, Object>();
        // TODO: upgrade. This looks at the TGT cookie to get the current user. Why not just use the current user from Spring security?
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
     *
     * @param model    model
     * @param username username
     * @return view
     */
    @RequestMapping
    public String forgot(Model model, @RequestParam(required = false) String username) {
        model.addAttribute("username", username);
        model.addAttribute("supportPhoneNumbers", supportContactService.getSupportPhoneNumbers());
        return "registration/" + getViewBase() + "forgot";
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
    @RequestMapping
    public String recover(Model model, String username, String recoveryCode) {
        log.info("password recovery request for email " + username);
        model.addAttribute("username", username);
        model.addAttribute("supportPhoneNumbers", supportContactService.getSupportPhoneNumbers());
        recoveryCode = StringUtils.trim(recoveryCode);

        if (StringUtils.isNotEmpty(recoveryCode)) {
            //Checking provided recovery code
            User user = userService.findUserByRecoveryCode(recoveryCode);

            if (user == null) {
                log.warn("invalid password recovery code was entered: " + recoveryCode);
                model.addAttribute("error", "forgotpassword.noSuchCode");
                return "registration/" + getViewBase() + "recover";
            } else {
                log.info("correct password recovery code was entered for user " + user.getId());
                model.addAttribute("recoveryCode", recoveryCode);
                return "registration/" + getViewBase() + "reset";
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
                return "registration/" + getViewBase() + "forgot";
            }

            return "registration/" + getViewBase() + "recover";
        } else {
            //Not requesting new code nor provided existing code
            return "registration/" + getViewBase() + "forgot";
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
    @RequestMapping
    public String reset(Model model, String recoveryCode, String password1, String password2, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("supportPhoneNumbers", supportContactService.getSupportPhoneNumbers());

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

            return "registration/" + getViewBase() + "reset";
        } else {
            if (user != null) {
                autoLoginService.autoLogin(user.getUsername(), request, response);
            }

            return "redirect:/app/central/home";
        }
    }

    @RequestMapping
    public
    @ResponseBody
    boolean checkPasswordForLast4WithRecoveryCode(String recoveryCode, String password1) {
        User user = userService.findUserByRecoveryCode(recoveryCode);

        return user != null && StringUtils.isNotBlank(password1) && !passwordService.lastFourPasswordsContains(user, password1);
    }

    @RequestMapping
    public
    @ResponseBody
    boolean checkPasswordForLast4WithOldPassword(String username, String currentPassword, String password1) {
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(username, currentPassword);

        return loginResult.getLoginStatus().isSuccessful() && StringUtils.isNotBlank(password1) && !passwordService.lastFourPasswordsContains(loginResult.getUser(), password1);
    }

    /**
     * Called from AJAX to get a URL to an app logo, if available.
     *
     * @param appType appType
     * @param appName appName
     * @return view
     * @throws IOException e
     */
    @RequestMapping
    @ResponseBody
    public String getLogoImageUrl(AppType appType, String appName) throws IOException {
        String url = "";

        try {
            if (appType != null && StringUtils.isNotEmpty(appName)) {
                if (appType.equals(AppType.CRM)) {
                    url = crmService.getLogoUrl(appName);
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
