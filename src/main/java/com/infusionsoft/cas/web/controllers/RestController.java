package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.api.domain.*;
import com.infusionsoft.cas.auth.LoginResult;
import com.infusionsoft.cas.dao.UserAccountDAO;
import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.PendingUserAccount;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.DuplicateAccountException;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.UserService;
import com.infusionsoft.cas.support.AppHelper;
import com.infusionsoft.cas.web.RestfulResponseBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.support.HandlerMethodInvocationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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
    private AppHelper appHelper;

    @Autowired
    private UserAccountDAO userAccountDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @Value("${infusionsoft.cas.apikey}")
    private String requiredApiKey;

    @RequestMapping(value = "userSearch", method = RequestMethod.GET)
    public ResponseEntity userSearch(@RequestParam String apiKey, @RequestParam(required = false) String userName, @RequestParam(defaultValue = "0", required = false) Integer pageNumberRequested, @RequestParam(defaultValue = "10", required = false) Integer pageSizeFromRequest, HttpServletRequest request) throws IOException {
        Locale localeFromRequest = request.getLocale();
        if(localeFromRequest == null || StringUtils.isBlank(localeFromRequest.getLanguage())){
            localeFromRequest = Locale.US;
        }
        ResponseEntity apiKeyResponse = validateApiKey(apiKey, localeFromRequest);
        if (apiKeyResponse != null) {
            return apiKeyResponse;
        }
        try{
            Page<User> pagedUsers = userService.findByUsernameLike(userName, new PageRequest(pageNumberRequested, pageSizeFromRequest));
            PageMetaData pageMetaData = new PageMetaData();
            pageMetaData.setTotalElements((int) pagedUsers.getTotalElements());
            pageMetaData.setTotalPages(pagedUsers.getTotalPages());
            pageMetaData.setSize(pageSizeFromRequest);
            pageMetaData.setNumber(pageNumberRequested);

            List<UserDTO> userDtos = new ArrayList<UserDTO>();
            for (User user : pagedUsers.getContent()) {
                UserDTO userDTO = new UserDTO(user, appHelper);
                userDtos.add(userDTO);
            }
            return new ResponseEntity<RestfulResponseBean>(new RestfulResponseBean(userDtos, pageMetaData), HttpStatus.OK);

        } catch(Exception e){
            return new ResponseEntity<APIErrorDTO>(new APIErrorDTO("cas.exception.user.search", messageSource, localeFromRequest), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Registers a new user account mapped to an app account.
     */
    @RequestMapping
    @ResponseBody
    public ResponseEntity linkAccount(String apiKey, @RequestParam(defaultValue = "0") long casGlobalId, /* TODO: make not optional when casGlobalId is gone */ @RequestParam(defaultValue = "0") long globalUserId, String appUsername, String appName, AppType appType, Locale locale) {
        // Validate the API key
        ResponseEntity apiKeyResponse = validateApiKey(apiKey, locale);
        if (apiKeyResponse != null) {
            return apiKeyResponse;
        }
        globalUserId = fallbackToCasGlobalId(globalUserId, casGlobalId);

        // Attempt the account linking
        User user;
        try {
            user = userService.loadUser(globalUserId);
            userService.associateAccountToUser(user, appType, appName, appUsername);
            return new ResponseEntity<UserDTO>(new UserDTO(user, appHelper), HttpStatus.OK);
        } catch (DuplicateAccountException e) {
            log.error(messageSource.getMessage("cas.exception.linkAccount.failure", new Object[]{globalUserId, appUsername, appName, appType}, Locale.US), e);
            AccountDTO[] duplicateAccountDTOs = AccountDTO.convertFromCollection(e.getDuplicateAccounts(), appHelper);
            return new ResponseEntity<APIErrorDTO>(new APIErrorDTO<AccountDTO[]>("cas.exception.conflict.user.account", messageSource, locale, duplicateAccountDTOs), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error(messageSource.getMessage("cas.exception.linkAccount.failure", new Object[]{globalUserId, appUsername, appName, appType}, Locale.US), e);
            return new ResponseEntity<APIErrorDTO>(new APIErrorDTO("cas.exception.linkAccount.failure", messageSource, new Object[]{globalUserId, appUsername, appName, appType}, locale), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This is to ensure that either casGlobalId or globalUserId can be passed in
     */
    // TODO - remove casGlobalId in favor of globalUserId in next revision of this API
    private long fallbackToCasGlobalId(long globalUserId, long casGlobalId) {
        return (casGlobalId > 0 && globalUserId == 0) ? casGlobalId : globalUserId;
    }

    /**
     * Finds and re-associates accounts that have been disassociated previously. This is for the case where the admin
     * of an Infusionsoft app deactivates and later reactivates one of their users.
     */
    @RequestMapping
    @ResponseBody
    public ResponseEntity reassociateAccounts(String apiKey, String appName, AppType appType, @RequestParam(required = false) String appUsername, @RequestParam(defaultValue = "0") long casGlobalId, @RequestParam(defaultValue = "0") long globalUserId, Locale locale) {
        // Validate the API key
        ResponseEntity apiKeyResponse = validateApiKey(apiKey, locale);
        if (apiKeyResponse != null) {
            return apiKeyResponse;
        }
        globalUserId = fallbackToCasGlobalId(globalUserId, casGlobalId);

        try {
            List<UserAccount> accounts;
            if (globalUserId > 0) {
                // Find any matching accounts by globalUserId and re-associate them
                accounts = userService.findDisabledUserAccounts(appName, appType, globalUserId);
                log.info("found " + accounts.size() + " disabled user accounts mapped to local user with globalUserId = " + globalUserId + " on " + appName + "/" + appType);
            } else {
                // Find any matching accounts by appUsername and re-associate them
                accounts = userService.findDisabledUserAccounts(appName, appType, appUsername);
                log.info("found " + accounts.size() + " disabled user accounts mapped to local user " + appUsername + " on " + appName + "/" + appType);
            }
            for (UserAccount account : accounts) {
                userService.enableUserAccount(account);
            }

            return new ResponseEntity<String>("OK", HttpStatus.OK);
        } catch (Exception e) {
            return logAndReturnError(e, "cas.exception.reassociateAccounts.failure", new Object[]{globalUserId, appUsername, appName, appType}, locale);
        }
    }

    /**
     * Disassociates an account or accounts from an Infusionsoft ID. This can be called from trusted systems when one of
     * their local users is deleted or deactivated, to also remove the mapping on the CAS side. It can also be called
     * when an app instance is deleted; in this case, if the appUsername is not passed then it will unlink ALL
     * accounts for that app instance.
     */
    @RequestMapping
    @ResponseBody
    public ResponseEntity disassociateAccounts(String apiKey, String appName, AppType appType, @RequestParam(required = false) String appUsername, @RequestParam(defaultValue = "0") long casGlobalId, @RequestParam(defaultValue = "0") long globalUserId, Locale locale) {
        // Validate the API key
        ResponseEntity apiKeyResponse = validateApiKey(apiKey, locale);
        if (apiKeyResponse != null) {
            return apiKeyResponse;
        }
        globalUserId = fallbackToCasGlobalId(globalUserId, casGlobalId);

        try {
            List<UserAccount> accounts;
            if (globalUserId > 0) {
                // Find any matching accounts by globalUserId and disassociate them
                accounts = userService.findEnabledUserAccounts(appName, appType, globalUserId);
                log.info("found " + accounts.size() + " user accounts mapped to local user with globalUserId = " + globalUserId + " on " + appName + "/" + appType);
            } else {
                // Find any matching accounts by appUsername and disassociate them
                accounts = userService.findEnabledUserAccounts(appName, appType, appUsername);
                log.info("found " + accounts.size() + " user accounts mapped to local user " + appUsername + " on " + appName + "/" + appType);
            }
            for (UserAccount account : accounts) {
                userService.disableAccount(account);
            }

            return new ResponseEntity<String>("OK", HttpStatus.OK);
        } catch (Exception e) {
            return logAndReturnError(e, "cas.exception.disassociateAccounts.failure", new Object[]{globalUserId, appUsername, appName, appType}, locale);
        }
    }

    /**
     * Removes an application account linkage from an Infusionsoft ID. This can be called from trusted systems when one
     * of their local users is deleted, to also remove the mapping on the CAS side. It can also be called
     * when an app instance is deleted; in this case, if the appUsername is not passed then it will unlink ALL
     * accounts for that app instance.
     */
    @RequestMapping
    @ResponseBody
    public ResponseEntity unlinkUserFromApp(String apiKey, String appName, AppType appType, @RequestParam(required = false) String appUsername, @RequestParam(defaultValue = "0") long casGlobalId, @RequestParam(defaultValue = "0") long globalUserId, Locale locale) {
        // Validate the API key
        ResponseEntity apiKeyResponse = validateApiKey(apiKey, locale);
        if (apiKeyResponse != null) {
            return apiKeyResponse;
        }
        globalUserId = fallbackToCasGlobalId(globalUserId, casGlobalId);

        try {
            List<UserAccount> accounts;
            if (globalUserId > 0) {
                // Find any matching accounts by globalUserId and unlink them
                accounts = userService.findUserAccounts(appName, appType, globalUserId);
                log.info("Found " + accounts.size() + " user accounts mapped to local user with globalUserId = " + globalUserId + " on " + appName + "/" + appType);
            } else {
                // Find any matching accounts by appUsername and unlink them
                accounts = userService.findUserAccounts(appName, appType, appUsername);
                log.info("Found " + accounts.size() + " user accounts mapped to local user " + appUsername + " on " + appName + "/" + appType);
            }
            for (UserAccount account : accounts) {
                userService.deleteAccount(account);
            }

            return new ResponseEntity<String>("OK", HttpStatus.OK);
        } catch (Exception e) {
            return logAndReturnError(e, "cas.exception.unlinkUserFromApp.failure", new Object[]{globalUserId, appUsername, appName, appType}, locale);
        }
    }

    /**
     * Changes the application username that is associated with a user
     */
    @RequestMapping
    @ResponseBody
    public ResponseEntity changeAssociatedAppUsername(String apiKey, @RequestParam(defaultValue = "0") long casGlobalId, /* TODO: make not optional when casGlobalId is gone */ @RequestParam(defaultValue = "0") long globalUserId, String appName, AppType appType, String newAppUsername, Locale locale) {
        // Validate the API key
        ResponseEntity apiKeyResponse = validateApiKey(apiKey, locale);
        if (apiKeyResponse != null) {
            return apiKeyResponse;
        }
        globalUserId = fallbackToCasGlobalId(globalUserId, casGlobalId);

        try {
            userService.changeAssociatedAppUsername(userService.loadUser(globalUserId), appName, appType, newAppUsername);
            return new ResponseEntity<String>("OK", HttpStatus.OK);
        } catch (Exception e) {
            return logAndReturnError(e, "cas.exception.changeAssociatedAppUsername.failure", new Object[]{newAppUsername, appName, appType, globalUserId}, locale);
        }
    }

    /**
     * Called from CAM or other clients to predefine a user account mapping.
     * They can then supply the user with a link including the registration code.
     * When the user follows that link and registers, their account will automatically
     * be associated.
     */
    @RequestMapping
    @ResponseBody
    public ResponseEntity scheduleNewUserRegistration(String apiKey, String appName, AppType appType, String appUsername, String firstName, String lastName, String email, Locale locale) {
        // Validate the API key
        ResponseEntity apiKeyResponse = validateApiKey(apiKey, locale);
        if (apiKeyResponse != null) {
            return apiKeyResponse;
        }

        Map<String, String> model = new HashMap<String, String>();
        // Create the pending registration and return the code
        try {
            PendingUserAccount account = userService.createPendingUserAccount(appType, appName, appUsername, firstName, lastName, email, false);

            log.info("created new user registration code " + account.getRegistrationCode() + " for app " + appName);

            model.put("status", "ok");
            model.put("registrationCode", account.getRegistrationCode());
            return new ResponseEntity<Map>(model, HttpStatus.OK);
        } catch (Exception e) {
            log.error("failed to schedule new user registration", e);

            model.put("status", "error");
            return new ResponseEntity<Map>(model, HttpStatus.INTERNAL_SERVER_ERROR);
            //TODO: use typed returns for this method; I didn't want to change this since CAM still uses it
        }
    }

    /**
     * Authenticates caller credentials against their CAS account.  Pass in a username and either a password or
     * an MD5 hash of the password.  Enforces account locking if there are too many wrong guesses.
     * Returns a JSON object with user info if successful.
     * <p/>
     * NOTE: this call does *not* use an API key, because the password is the authentication
     */
    @RequestMapping(value = "authenticateUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity authenticateUser(String username, String password, String md5password, Locale locale) throws IOException {
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
                    case OldPassword:
                        int failedLoginAttempts = loginResult.getFailedAttempts();
                        if (failedLoginAttempts > InfusionsoftAuthenticationService.ALLOWED_LOGIN_ATTEMPTS) {
                            error = "login.lockedTooManyFailures";
                        } else if (failedLoginAttempts == 0) { // This happens if an old password is matched
                            error = "login.failed1";
                        } else {
                            error = "login.failed" + failedLoginAttempts;
                        }
                        break;
                    case PasswordExpired:
                        error = "login.passwordExpired";
                        break;
                    case Success:
                        error = null;
                        break;
                    default:
                        throw new IllegalStateException("Unknown value for loginResult: " + loginResult);
                }
            }

            if (error == null) {
                UserDTO userDTO = new UserDTO(loginResult.getUser(), appHelper);
                // TODO: This is a hack to make it lowercase.  Remove when mobile app does case-insensitive comparisons
                for (UserAccountDTO userAccountDTO : userDTO.getLinkedApps()) {
                    userAccountDTO.setAppType(StringUtils.lowerCase(userAccountDTO.getAppType()));
                }
                return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<APIErrorDTO>(new APIErrorDTO(error, messageSource, locale), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return logAndReturnError(e, "cas.exception.authenticateUser.failure", new Object[]{username}, locale);
        }
    }

    /**
     * Called from trusted clients to get info about a CAS user profile. This can be obtained either by the username
     * (the Infusionsoft ID of the user they are searching for), by the Global User ID, or by a combination of local
     * appName, appType, and appUsername.  Only returns enabled accounts.
     * <br/>
     * NOTE: One of these is required:  username OR globalUserId OR (appName/appType/appUsername)
     */
    @RequestMapping
    @ResponseBody
    public ResponseEntity getUserInfo(String apiKey, @RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "0") long casGlobalId, @RequestParam(defaultValue = "0") long globalUserId, @RequestParam(defaultValue = "") String appName, @RequestParam(defaultValue = "") AppType appType, @RequestParam(defaultValue = "") String appUsername, Locale locale) {
        // Validate the API key
        ResponseEntity apiKeyResponse = validateApiKey(apiKey, locale);
        if (apiKeyResponse != null) {
            return apiKeyResponse;
        }
        globalUserId = fallbackToCasGlobalId(globalUserId, casGlobalId);

        try {
            // Lookup the user
            User user = null;
            if (globalUserId > 0) {
                user = userService.loadUser(globalUserId);
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
                return new ResponseEntity<UserDTO>(new UserDTO(user, appHelper), HttpStatus.OK);
            } else {
                return new ResponseEntity<JSONObject>(new JSONObject(), HttpStatus.OK);
            }
        } catch (Exception e) {
            return logAndReturnError(e, "cas.exception.getUserInfo.failure", new Object[]{username, globalUserId, appName, appType, appUsername}, locale);
        }
    }

    private ResponseEntity<APIErrorDTO> validateApiKey(String apiKey, Locale locale) {
        // Validate the API key
        // TODO: replace this with a separate Spring security authentication entry point so the logic doesn't have to be embedded here
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            return new ResponseEntity<APIErrorDTO>(new APIErrorDTO("cas.exception.invalid.apikey", messageSource, locale), HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    private ResponseEntity<APIErrorDTO> logAndReturnError(Exception e, String code, Object[] args, Locale locale) {
        String errorMessage = messageSource.getMessage(code, args, locale);
        log.error(errorMessage, e);
        return new ResponseEntity<APIErrorDTO>(new APIErrorDTO(code, errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public APIErrorDTO exceptionHandler(org.springframework.security.web.firewall.FirewalledRequest request, HttpServletResponse response, Locale locale, Throwable ex) {
        if (ex instanceof HandlerMethodInvocationException) {
            log.warn("Exception in " + request.getPathInfo(), ex);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new APIErrorDTO("cas.exception.invalid.call", messageSource, new Object[]{ex.getCause().getMessage()}, locale);
        } else if (ex instanceof TypeMismatchException) {
            log.warn("Exception in " + request.getPathInfo(), ex);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new APIErrorDTO("cas.exception.invalid.call", messageSource, new Object[]{ex.getMessage()}, locale);
        } else {
            log.error("Exception in " + request.getPathInfo(), ex);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new APIErrorDTO("cas.exception.general", messageSource, new Object[]{ex.getMessage()}, locale);
        }
    }

}
