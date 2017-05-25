package org.apereo.cas.infusionsoft.web.controllers;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.api.APIErrorDTO;
import org.apereo.cas.api.UserDTO;
import org.apereo.cas.infusionsoft.authentication.LoginResult;
import org.apereo.cas.infusionsoft.services.AuditService;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.support.HandlerMethodInvocationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Really simple controller to allow authentication.
 */
@RestController
@RequestMapping(value = "/user/authenticate")
public class AuthenticateController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticateController.class);

    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private AppHelper appHelper;
    private UserService userService;
    private MessageSource messageSource;
    private AuditService auditService;

    public AuthenticateController(InfusionsoftAuthenticationService infusionsoftAuthenticationService, AppHelper appHelper, UserService userService, MessageSource messageSource, AuditService auditService) {
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
        this.appHelper = appHelper;
        this.userService = userService;
        this.messageSource = messageSource;
        this.auditService = auditService;
    }

    /**
     * Authenticates caller credentials against their CAS account.  Pass in a username and either a password or
     * an MD5 hash of the password.  Enforces account locking if there are too many wrong guesses.
     * Returns a JSON object with user info if successful.
     * <p>
     * NOTE: this call does *not* use an API key, because the password is the authentication
     *
     * @param username    username
     * @param password    password
     * @param md5password md5password
     * @param locale      locale
     * @return ResponseEntity
     * @throws IOException e
     */
    @RequestMapping(method = RequestMethod.POST)
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
                UserDTO userDTO = new UserDTO(loginResult.getUser(), userService.findActiveUserAccounts(loginResult.getUser()), appHelper);

                auditService.logApiLoginSuccess(loginResult.getUser());

                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            } else {
                auditService.logApiLoginFailure(username);

                return new ResponseEntity<>(new APIErrorDTO(error, messageSource, locale), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return logAndReturnError(e, "cas.exception.authenticateUser.failure", new Object[]{username}, locale);
        }
    }

    private ResponseEntity<APIErrorDTO> logAndReturnError(Exception e, String code, Object[] args, Locale locale) {
        String errorMessage = messageSource.getMessage(code, args, locale);
        log.error(errorMessage, e);
        return new ResponseEntity<>(new APIErrorDTO(code, errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public APIErrorDTO exceptionHandler(HttpServletRequest request, HttpServletResponse response, Locale locale, Throwable ex) {
        if (ex instanceof HandlerMethodInvocationException) {
            log.warn("Exception in " + request.getRequestURI(), ex);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new APIErrorDTO("cas.exception.invalid.call", messageSource, new Object[]{ex.getCause().getMessage()}, locale);
        } else if (ex instanceof TypeMismatchException) {
            log.warn("Exception in " + request.getRequestURI(), ex);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new APIErrorDTO("cas.exception.invalid.call", messageSource, new Object[]{ex.getMessage()}, locale);
        } else {
            log.error("Exception in " + request.getRequestURI(), ex);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new APIErrorDTO("cas.exception.general", messageSource, new Object[]{ex.getMessage()}, locale);
        }
    }

}
