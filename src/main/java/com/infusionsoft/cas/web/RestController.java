package com.infusionsoft.cas.web;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.InfusionsoftDataService;
import com.infusionsoft.cas.services.InfusionsoftPasswordService;
import com.infusionsoft.cas.types.MigratedApp;
import com.infusionsoft.cas.types.PendingUserAccount;
import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.services.ServiceRegistryDao;
import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Really simple controller that provides REST-like JSON services for registering users.
 * REST purists, please don't be offended by the use of the POST verb to create a new user.
 * We just want something easy that does the job.
 */
@Controller
public class RestController extends MultiActionController {
    private static final Logger log = Logger.getLogger(RestController.class);

    private static final int PASSWORD_LENGTH_MIN = 7;
    private static final int PASSWORD_LENGTH_MAX = 20;

    private HibernateTemplate hibernateTemplate;
    private PasswordEncoder passwordEncoder;
    private UniqueTicketIdGenerator ticketIdGenerator;
    private CentralAuthenticationService centralAuthenticationService;
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private InfusionsoftDataService infusionsoftDataService;
    private InfusionsoftPasswordService infusionsoftPasswordService;
    private ServiceRegistryDao serviceRegistryDao;
    private String requiredApiKey;

    public RestController() {
    }

    /**
     * Registers a new user account and returns a simple JSON object.
     */
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        String apiKey = request.getParameter("apiKey");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            logger.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Attempt the registration
        try {
            User user = new User();

            user.setUsername(username);
            user.setEnabled(true);

            if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.put("error", "registration.error.invalidUsername");
            } else if (hibernateTemplate.find("from User u where u.username = ?", username).size() > 0) {
                model.put("error", "registration.error.usernameInUse");
            } else if (password == null || password.length() < PASSWORD_LENGTH_MIN || password.length() > PASSWORD_LENGTH_MAX) {
                model.put("error", "registration.error.invalidPassword");
            } else {
                String passwordError = infusionsoftPasswordService.validatePassword(user, username, password);

                if (passwordError != null) {
                    model.put("error", passwordError);
                }
            }

            if (model.containsKey("error")) {
                logger.warn("couldn't create new user account via REST service for API key " + apiKey + ": " + model.get("error"));
            } else {
                model.put("user", user);

                hibernateTemplate.save(user);
                infusionsoftPasswordService.setPasswordForUser(user, password);
            }
        } catch (Exception e) {
            logger.error("failed to create user account", e);

            model.put("error", "registration.error.exception");
        }

        // Render the response
        try {
            // TODO - include an error message
            if (model.containsKey("error")) {
                model.put("status", "error");
            } else {
                model.put("status", "ok");
            }

            MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
            MediaType jsonMimeType = MediaType.APPLICATION_JSON;

            jsonConverter.write(model, jsonMimeType, new ServletServerHttpResponse(response));
        } catch (Exception e) {
            logger.error("Failed to render JSON response", e);
        }

        return null;
    }

    /**
     * Registers a new user account mapped to an app account,
     * and returns a simple JSON object.
     */
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
            logger.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Attempt the registration
        try {
            User user = new User();

            user.setUsername(username);
            user.setEnabled(true);

            if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.put("error", "registration.error.invalidUsername");
            } else if (hibernateTemplate.find("from User u where u.username = ?", username).size() > 0) {
                model.put("error", "registration.error.usernameInUse");
            } else if (password == null || password.length() < PASSWORD_LENGTH_MIN || password.length() > PASSWORD_LENGTH_MAX) {
                model.put("error", "registration.error.invalidPassword");
            } else if (!appType.equals("crm") || appType.equals("community") || appType.equals("customerhub")) {
                model.put("error", "registration.error.invalidAppType");
            } else if (StringUtils.isEmpty(appName)) {
                model.put("error", "registration.error.invalidAppName");
            } else {
                String passwordError = infusionsoftPasswordService.validatePassword(user, username, password);

                if (passwordError != null) {
                    model.put("error", passwordError);
                }
            }

            if (model.containsKey("error")) {
                logger.warn("couldn't create new user account via REST service for API key " + apiKey + ": " + model.get("error"));
            } else {
                UserAccount account = new UserAccount();

                account.setAppName(appName);
                account.setAppType(appType);
                account.setAppUsername(appUsername);
                account.setUser(user);

                model.put("user", user);

                user.getAccounts().add(account);

                hibernateTemplate.save(user);
                infusionsoftPasswordService.setPasswordForUser(user, password);
            }
        } catch (Exception e) {
            logger.error("failed to create user account", e);

            model.put("error", "registration.error.exception");
        }

        // Render the response
        try {
            // TODO - include an error message
            if (model.containsKey("error")) {
                model.put("status", "error");
            } else {
                model.put("status", "ok");
            }

            MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
            MediaType jsonMimeType = MediaType.APPLICATION_JSON;

            jsonConverter.write(model, jsonMimeType, new ServletServerHttpResponse(response));
        } catch (Exception e) {
            logger.error("Failed to render JSON response", e);
        }

        return null;
    }

    /**
     * Notifies CAS that a new app has been created. This is what enables it to know which apps were created post-CAS,
     * so we don't have to worry about the migration flow for those apps.
     */
    public ModelAndView registerNewApp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        String apiKey = request.getParameter("apiKey");
        String appName = request.getParameter("appName");
        String appType = request.getParameter("appType");

        // Validate the API key
        if (!requiredApiKey.equals(apiKey)) {
            logger.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        try {
            MigratedApp app = new MigratedApp();

            app.setAppName(appName);
            app.setAppType(appType);
            app.setDateMigrated(new Date());

            hibernateTemplate.save(app);

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
     * Called from CAM or other clients to predefine a user account mapping.
     * They can then supply the user with a link including the registration code.
     * When the user follows that link and registers, their account will automatically
     * be associated.
     */
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
            logger.warn("Invalid API access: apiKey = " + apiKey);
            response.sendError(401);

            return null;
        }

        // Create the pending registration and return the code
        try {
            PendingUserAccount account = infusionsoftDataService.createPendingUserAccount(appType, appName, appUsername, firstName, lastName, email, false);

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

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setTicketIdGenerator(UniqueTicketIdGenerator ticketIdGenerator) {
        this.ticketIdGenerator = ticketIdGenerator;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setCentralAuthenticationService(CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    public void setInfusionsoftAuthenticationService(InfusionsoftAuthenticationService infusionsoftAuthenticationService) {
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
    }

    public void setInfusionsoftPasswordService(InfusionsoftPasswordService infusionsoftPasswordService) {
        this.infusionsoftPasswordService = infusionsoftPasswordService;
    }

    public void setInfusionsoftDataService(InfusionsoftDataService infusionsoftDataService) {
        this.infusionsoftDataService = infusionsoftDataService;
    }

    public void setRequiredApiKey(String requiredApiKey) {
        this.requiredApiKey = requiredApiKey;
    }
}
