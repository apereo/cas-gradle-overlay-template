package com.infusionsoft.cas.web;

import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.apache.commons.validator.EmailValidator;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.services.ServiceRegistryDao;
import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Really simple controller that provides REST-like JSON services for registering users.
 * REST purists, please don't be offended by the use of the POST verb to create a new user.
 * We just want something easy that does the job.
 */
@Controller
public class RestController extends MultiActionController {
    private static final int PASSWORD_LENGTH_MIN = 7;
    private static final int PASSWORD_LENGTH_MAX = 20;

    private HibernateTemplate hibernateTemplate;
    private PasswordEncoder passwordEncoder;
    private UniqueTicketIdGenerator ticketIdGenerator;
    private CentralAuthenticationService centralAuthenticationService;
    private ServiceRegistryDao serviceRegistryDao;
    private String requiredApiKey = "bl4h"; // TODO - inject this

    /**
     * Registers a new user account and returns a simple JSON object.
     */
    @RequestMapping(value="/registerUser", method = RequestMethod.POST)
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
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(true);

            if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.put("error", "registration.error.invalidUsername");
            } else if (hibernateTemplate.find("from User u where u.username = ?", username).size() > 0) {
                model.put("error", "registration.error.usernameInUse");
            } else if (password == null || password.length() < PASSWORD_LENGTH_MIN || password.length() > PASSWORD_LENGTH_MAX) {
                model.put("error", "registration.error.invalidPassword");
            }

            if (model.containsKey("error")) {
                logger.warn("couldn't create new user account via REST service for API key " + apiKey + ": " + model.get("error"));
            } else {
                model.put("user", user);

                hibernateTemplate.save(user);
            }
        } catch (Exception e) {
            logger.error("failed to create user account", e);

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
            logger.error("Failed to render JSON response", e);
        }

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
}
