package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.auth.LoginResult;
import com.infusionsoft.cas.dao.UserAccountDAO;
import com.infusionsoft.cas.domain.*;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.MigrationService;
import com.infusionsoft.cas.services.PasswordService;
import com.infusionsoft.cas.services.UserService;
import com.infusionsoft.cas.support.JsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Really simple controller that provides HTTP JSON services for registering users.
 * REST purists, please don't be offended by the use of the POST verb to create or authenticate users.
 * We just want something easy that does the job.
 */
// TODO - make better and more consistent use of HTTP 4xx error codes throughout
@Controller
public class RestController {
    private static final Logger log = Logger.getLogger(RestController.class);

    private static final int PASSWORD_LENGTH_MIN = 7;
    private static final int PASSWORD_LENGTH_MAX = 20;

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    JsonHelper jsonHelper;

    @Autowired
    MigrationService migrationService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    UserAccountDAO userAccountDAO;

    @Autowired
    UserService userService;

    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    CookieRetrievingCookieGenerator cookieRetrievingCookieGenerator;

    @Value("${infusionsoft.cas.apikey}")
    private String requiredApiKey;

    /**
     * Registers a new user account and returns a simple JSON object.
     */
    // TODO - pretty sure nothing uses this. Should probably delete.
    @RequestMapping
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        String apiKey = request.getParameter("apiKey");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Attempt the registration
        try {
            User user = new User();

            user.setUsername(username);
            user.setEnabled(true);
            user.setPassword(password);

            if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.put("error", "registration.error.invalidUsername");
            } else if (userService.loadUser(username) != null) {
                model.put("error", "registration.error.usernameInUse");
            } else if (password == null || password.length() < PASSWORD_LENGTH_MIN || password.length() > PASSWORD_LENGTH_MAX) {
                model.put("error", "registration.error.invalidPassword");
            } else {
                String passwordError = passwordService.validatePassword(user);

                if (passwordError != null) {
                    model.put("error", passwordError);
                }
            }

            if (model.containsKey("error")) {
                log.warn("couldn't create new user account via REST service for API key " + apiKey + ": " + model.get("error"));
            } else {
                model.put("user", user);

                userService.addUser(user);
//                userDAO.save(user);
//                passwordService.setPasswordForUser(user, password);
            }
        } catch (Exception e) {
            log.error("failed to create user account", e);

            model.put("error", "registration.error.exception");
        }

        // Render the response
        try {
            if (model.containsKey("error")) {
                model.put("status", "error");
            } else {
                model.put("status", "ok");
            }

            MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
            MediaType jsonMimeType = MediaType.APPLICATION_JSON;

            jsonConverter.write(model, jsonMimeType, new ServletServerHttpResponse(response));
        } catch (Exception e) {
            log.error("Failed to render JSON response", e);
        }

        return null;
    }

    /**
     * Registers a new user account mapped to an app account,
     * and returns a simple JSON object.
     */
    @RequestMapping
    public ModelAndView registerUserWithApp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        String apiKey = request.getParameter("apiKey");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String appUsername = request.getParameter("appUsername");
        String appName = request.getParameter("appName");
        String appType = request.getParameter("appType"); // crm, community, customerhub

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Attempt the registration
        try {
            User user = new User();

            user.setUsername(username);
            user.setEnabled(true);
            user.setPassword(password);

            if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.put("error", "registration.error.invalidUsername");
            } else if (userService.loadUser(username) != null) {
                model.put("error", "registration.error.usernameInUse");
            } else if (password == null || password.length() < PASSWORD_LENGTH_MIN || password.length() > PASSWORD_LENGTH_MAX) {
                model.put("error", "registration.error.invalidPassword");
            } else if (!appType.equals(AppType.CRM) || appType.equals(AppType.COMMUNITY) || appType.equals(AppType.CUSTOMERHUB)) {
                model.put("error", "registration.error.invalidAppType");
            } else if (StringUtils.isEmpty(appName)) {
                model.put("error", "registration.error.invalidAppName");
            } else {
                String passwordError = passwordService.validatePassword(user);

                if (passwordError != null) {
                    model.put("error", passwordError);
                }
            }

            if (model.containsKey("error")) {
                log.warn("couldn't create new user account via REST service for API key " + apiKey + ": " + model.get("error"));
            } else {
                model.put("user", user);

                userService.addUser(user);
                userService.associateAccountToUser(user, appType, appName, appUsername, cookieRetrievingCookieGenerator.retrieveCookieValue(request));
            }
        } catch (Exception e) {
            log.error("failed to create user account", e);

            model.put("error", "registration.error.exception");
        }

        // Render the response
        try {
            if (model.containsKey("error")) {
                model.put("status", "error");
            } else {
                model.put("status", "ok");
            }

            MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
            MediaType jsonMimeType = MediaType.APPLICATION_JSON;

            jsonConverter.write(model, jsonMimeType, new ServletServerHttpResponse(response));
        } catch (Exception e) {
            log.error("Failed to render JSON response", e);
        }

        return null;
    }

    /**
     * Notifies CAS that a new app has been created. This is what enables it to know which apps were created post-CAS,
     * so we don't have to worry about the migration flow for those apps.
     */
    @RequestMapping
    public ModelAndView registerNewApp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        String apiKey = request.getParameter("apiKey");
        String appName = request.getParameter("appName");
        String appType = request.getParameter("appType");

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        try {
            MigratedApp app = new MigratedApp();

            app.setAppName(appName);
            app.setAppType(appType);
            app.setDateMigrated(new Date());

            migrationService.save(app);

            model.put("status", "success");
        } catch (Exception e) {
            log.error("unable to save migrated app " + appName + "/" + appType, e);

            model.put("status", "error");
            model.put("message", "couldn't save the migrated app! make sure appName and appType are valid and it hasn't been migrated");
        }

        MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        jsonConverter.write(model, jsonMimeType, new ServletServerHttpResponse(response));

        return null;
    }

    /**
     * Finds and reassociates accounts that have been disassociated previously. This is for the case where the admin
     * of an Infusionsoft app deactivates and later reactivates one of their users.
     */
    // TODO - consider making this return JSON responses similar to the other API calls
    @RequestMapping
    public ModelAndView reassociateAccounts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String apiKey = request.getParameter("apiKey");
        String appName = request.getParameter("appName");
        String appType = request.getParameter("appType");
        String appUsername = request.getParameter("appUsername");

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Find any matching accounts and reassociate them
        try {
            List<UserAccount> accounts = userService.findDisabledUserAccounts(appName, appType, appUsername);

            log.info("found " + accounts.size() + " disabled user accounts mapped to local user " + appUsername + " on " + appName + "/" + appType);

            for (UserAccount account : accounts) {
                userService.enableUserAccount(account);
            }

            response.getWriter().append("OK");
        } catch (Exception e) {
            log.error("failed to reassociate user accounts for " + appUsername + " on " + appName + "/" + appType);

            response.sendError(500);
        }

        return null;
    }

    /**
     * Disassociates an account or accounts from an Infusionsoft ID. This can be called from trusted systems when one of
     * their local users is deleted or deactivated, to also remove the mapping on the CAS side. It can also be called
     * when an app instance is deleted; in this case, if the appUsername is not passed then it will unlink ALL
     * accounts for that app instance.
     */
    // TODO - consider making this return JSON responses similar to the other API calls
    @RequestMapping
    public ModelAndView disassociateAccounts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String apiKey = request.getParameter("apiKey");
        String appName = request.getParameter("appName");
        String appType = request.getParameter("appType");
        String appUsername = request.getParameter("appUsername");

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Find any matching accounts and disassociate them
        try {
            List<UserAccount> accounts = userService.findEnabledUserAccounts(appName, appType, appUsername);

            log.info("found " + accounts.size() + " user accounts mapped to local user " + appUsername + " on " + appName + "/" + appType);

            for (UserAccount account : accounts) {
                userService.disableAccount(account);
            }

            response.getWriter().append("OK");
        } catch (Exception e) {
            log.error("failed to unlink user accounts mapped to local user " + appUsername + " on " + appName + "/" + appType);

            response.sendError(500);
        }

        return null;
    }

    /**
     * Changes the application username that is associated with a user
     */
    // TODO - consider making this return JSON responses similar to the other API calls
    @RequestMapping
    public ModelAndView changeAssociatedAppUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String apiKey = request.getParameter("apiKey");
        String username = request.getParameter("username");
        String appName = request.getParameter("appName");
        String appType = request.getParameter("appType");
        String oldAppUsername = request.getParameter("oldAppUsername");
        String newAppUsername = request.getParameter("newAppUsername");

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);
        } else {
            try {
                userService.changeAssociatedAppUsername(username, appName, appType, oldAppUsername, newAppUsername);
                response.getWriter().append("OK");
            } catch (Exception e) {
                log.error("Failed to change application username from " + oldAppUsername + " to " + newAppUsername + " for user " + username + "on " + appName + "/" + appType, e);
                response.sendError(500);
            }
        }

        return null;
    }

    /**
     * Called from CAM or other clients to predefine a user account mapping.
     * They can then supply the user with a link including the registration code.
     * When the user follows that link and registers, their account will automatically
     * be associated.
     */
    @RequestMapping
    public ModelAndView scheduleNewUserRegistration(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        String apiKey = request.getParameter("apiKey");
        String appName = request.getParameter("appName");
        String appType = request.getParameter("appType");
        String appUsername = request.getParameter("appUsername");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Create the pending registration and return the code
        try {
            PendingUserAccount account = userService.createPendingUserAccount(appType, appName, appUsername, firstName, lastName, email, false);

            log.info("created new user registration code " + account.getRegistrationCode() + " for app " + appName);

            model.put("status", "ok");
            model.put("registrationCode", account.getRegistrationCode());
        } catch (Exception e) {
            log.error("failed to schedule new user registration", e);

            model.put("status", "error");
        }

        MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        jsonConverter.write(model, jsonMimeType, new ServletServerHttpResponse(response));

        return null;
    }

    /**
     * Authenticates caller credentials against their CAS account.  Pass in a username and either a password or
     * an MD5 hash of the password.  Enforces account locking if there are too many wrong guesses.
     * Returns a JSON object with user info if successful.
     */
    @RequestMapping(value = "/authenticateUser", method = RequestMethod.POST)
    public ModelAndView authenticateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String md5password = request.getParameter("md5password");

        String error = null;
        LoginResult loginResult = null;

        try {
            if (StringUtils.isEmpty(username)) {
                error = "login.noUsername";
            } else if (StringUtils.isEmpty(password) && StringUtils.isEmpty(md5password)) {
                error = "login.noPassword";
            }

            if (error == null) {
                if (StringUtils.isNotEmpty(password)) {
                    loginResult = infusionsoftAuthenticationService.attemptLogin(username, password);
                } else {
                    loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(username, md5password);
                }

                switch (loginResult.getLoginStatus()) {
                    case AccountLocked:
                        error = "login.lockedTooManyFailures";
                        break;
                    case BadPassword:
                    case DisabledUser:
                    case NoSuchUser:
                        int failedLoginAttempts = infusionsoftAuthenticationService.countConsecutiveFailedLogins(username);
                        error = "login.failed" + failedLoginAttempts;
                        break;
                    case PasswordExpired:
                        error = "login.passwordExpired";
                        break;
                    case Success:
                        error = null;
                        break;
                    default:
                        log.error("Unknown value for loginResult: " + loginResult);
                        error = "Unknown value for loginResult: " + loginResult;
                        break;
                }
            }

            if (error == null) {
                response.setStatus(200);
                response.setContentType("application/json");
                response.getWriter().write(jsonHelper.buildUserInfoJSON(loginResult.getUser()));
            } else {
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write(jsonHelper.buildErrorJson(error));
            }
        } catch (Exception e) {
            log.error("failed to create JSON response", e);
            response.sendError(500);
        }

        return null;
    }

    /**
     * Called from trusted clients to get info about a CAS user profile. This can be obtained either by the username
     * (the Infusionsoft ID of the user they are searching for), by the CAS global ID, or by a combination of local
     * appName, appType, and appUsername.  Only returns enabled accounts.
     */
    @RequestMapping
    public ModelAndView getUserInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String apiKey = request.getParameter("apiKey");
        // One of:
        String username = request.getParameter("username");
        // OR
        String casGlobalIdString = request.getParameter("casGlobalId");
        // OR
        String appName = request.getParameter("appName");
        String appType = request.getParameter("appType");
        String appUsername = request.getParameter("appUsername");

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Parse the casGlobalId
        long casGlobalId = NumberUtils.toLong(casGlobalIdString);

        // Lookup the user
        User user = null;
        if (casGlobalId > 0) {
            user = userService.loadUser(casGlobalId);
            if (user != null && !user.isEnabled())
                user = null;
        } else if (StringUtils.isNotEmpty(username)) {
            user = userService.findEnabledUser(username);
        } else {
            List<UserAccount> accounts = userAccountDAO.findByAppNameAndAppTypeAndAppUsernameAndDisabled(appName, appType, appUsername, false);

            // Return the first match, even if multiple users are mapped to the account
            if (accounts.size() > 0) {
                user = accounts.get(0).getUser();
            } else {
                log.info("could not find an active user account for " + appUsername + " on " + appName + "/" + appType);
            }
        }

        if (user != null) {
            response.setContentType("application/json");
            response.getWriter().println(jsonHelper.buildUserInfoJSON(user));
        } else {
            response.setContentType("application/json");
            response.getWriter().println(new JSONObject().toJSONString());
        }

        return null;
    }

}
