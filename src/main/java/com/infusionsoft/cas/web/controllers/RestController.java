package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.auth.LoginResult;
import com.infusionsoft.cas.dao.UserAccountDAO;
import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.PendingUserAccount;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.UserService;
import com.infusionsoft.cas.support.JsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private JsonHelper jsonHelper;

    @Autowired
    private UserAccountDAO userAccountDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @Value("${infusionsoft.cas.apikey}")
    private String requiredApiKey;

    /**
     * Registers a new user account mapped to an app account,
     * and returns a simple JSON object.
     */
    @RequestMapping
    @ResponseBody
    public String linkAccount(String apiKey, Long casId, String appUsername, String appName, AppType appType, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Attempt the registration
        User user;
        try {
            user = userService.loadUser(casId);
            userService.associateAccountToUser(user, appType, appName, appUsername);
        } catch (Exception e) {
            log.error("failed to create user account", e);
            throw new Exception(messageSource.getMessage("registration.error.linkAccount", new Object[]{casId}, request.getLocale()));
        }

        return jsonHelper.buildUserInfoJSON(user);
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
        AppType appType = AppType.valueOf(request.getParameter("appType"));
        String appUsername = request.getParameter("appUsername");
        String casGlobalIdString = request.getParameter("casGlobalId");

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Parse the casGlobalId
        long casGlobalId = NumberUtils.toLong(casGlobalIdString);

        try {

            // Find any matching accounts by casGlobalId and reassociate them
            List<UserAccount> accounts;
            if (casGlobalId > 0) {
                accounts = userService.findDisabledUserAccounts(appName, appType, casGlobalId);
                log.info("found " + accounts.size() + " disabled user accounts mapped to local user with casGlobalId = " + casGlobalId + " on " + appName + "/" + appType);
                for (UserAccount account : accounts) {
                    userService.enableUserAccount(account);
                }
            }

            // Find any remaining matching accounts and reassociate them
            accounts = userService.findDisabledUserAccounts(appName, appType, appUsername);
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
        AppType appType = AppType.valueOf(request.getParameter("appType"));
        String appUsername = request.getParameter("appUsername");
        String casGlobalIdString = request.getParameter("casGlobalId");

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Parse the casGlobalId
        long casGlobalId = NumberUtils.toLong(casGlobalIdString);

        try {

            // Find any matching accounts by casGlobalId and disassociate them
            List<UserAccount> accounts;
            if (casGlobalId > 0) {
                accounts = userService.findEnabledUserAccounts(appName, appType, casGlobalId);
                log.info("found " + accounts.size() + " user accounts mapped to local user with casGlobalId = " + casGlobalId + " on " + appName + "/" + appType);
                for (UserAccount account : accounts) {
                    userService.disableAccount(account);
                }
            }

            // Find any remaining matching accounts and disassociate them
            accounts = userService.findEnabledUserAccounts(appName, appType, appUsername);
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
     * Removes an application account linkage from an Infusionsoft ID. This can be called from trusted systems when one
     * of their local users is deleted, to also remove the mapping on the CAS side. It can also be called
     * when an app instance is deleted; in this case, if the appUsername is not passed then it will unlink ALL
     * accounts for that app instance.
     */
    // TODO - consider making this return JSON responses similar to the other API calls
    @RequestMapping
    public ModelAndView unlinkUserFromApp(HttpServletResponse response, String apiKey, String appName, AppType appType, @RequestParam(required = false) String appUsername, long casGlobalId) throws IOException {
        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            return reportError(response, 401, Level.WARN, "Invalid API access: apiKey = " + apiKey);
        }

        try {
            // Find any matching accounts by casGlobalId and unlink them
            List<UserAccount> accounts;
            if (casGlobalId > 0) {
                accounts = userService.findUserAccounts(appName, appType, casGlobalId);
                log.info("Found " + accounts.size() + " user accounts mapped to local user with casGlobalId = " + casGlobalId + " on " + appName + "/" + appType);
                for (UserAccount account : accounts) {
                    userService.deleteAccount(account);
                }
            }

            // Find any remaining matching accounts and unlink them
            accounts = userService.findUserAccounts(appName, appType, appUsername);
            log.info("Found " + accounts.size() + " user accounts mapped to local user " + appUsername + " on " + appName + "/" + appType);
            for (UserAccount account : accounts) {
                userService.deleteAccount(account);
            }

            response.getWriter().append("OK");
        } catch (Exception e) {
            return reportError(response, 500, Level.ERROR, "Failed to delete user accounts mapped to local user " + appUsername + " on " + appName + "/" + appType);
        }

        return null;
    }

    private ModelAndView reportError(HttpServletResponse response, int statusCode, Level level, String errorMessage) throws IOException {
        log.log(level, errorMessage);
        response.sendError(statusCode);
        return null;
    }

    /**
     * Changes the application username that is associated with a user
     */
    // TODO - consider making this return JSON responses similar to the other API calls
    @RequestMapping
    public ModelAndView changeAssociatedAppUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String apiKey = request.getParameter("apiKey");
        String casGlobalIdString = request.getParameter("casGlobalId");
        String appName = request.getParameter("appName");
        AppType appType = AppType.valueOf(request.getParameter("appType"));
        String newAppUsername = request.getParameter("newAppUsername");

        // Parse the casGlobalId
        long casGlobalId = NumberUtils.toLong(casGlobalIdString);

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);
        } else {
            try {
                userService.changeAssociatedAppUsername(userService.loadUser(casGlobalId), appName, appType, newAppUsername);
                response.getWriter().append("OK");
            } catch (Exception e) {
                log.error("Failed to change application username to " + newAppUsername + "on " + appName + "/" + appType + " for CAS user " + casGlobalId, e);
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
        AppType appType = AppType.valueOf(request.getParameter("appType"));
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
     * <p/>
     * NOTE: this call does *not* use an API key, because the password is the authentication
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
        AppType appType = AppType.valueOf(request.getParameter("appType"));
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
