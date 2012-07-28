package com.infusionsoft.cas.web;

import com.infusionsoft.cas.services.InfusionsoftMailService;
import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.util.UniqueTicketIdGenerator;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller that powers the registration and association pages.
 */
public class RegistrationMultiActionController extends MultiActionController {
    private static final Logger log = Logger.getLogger(RegistrationMultiActionController.class);

    private static final int PASSWORD_LENGTH_MIN = 7;
    private static final int PASSWORD_LENGTH_MAX = 20;
    private static final String FORUM_API_KEY = "bec0124123e5ab4c2ce362461cb46ff0";

    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private InfusionsoftMailService infusionsoftMailService;
    private HibernateTemplate hibernateTemplate;
    private PasswordEncoder passwordEncoder;
    private UniqueTicketIdGenerator ticketIdGenerator;

    /**
     * Shows the registration form.
     */
    public ModelAndView welcome(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();

        model.put("loginTicket", ticketIdGenerator.getNewTicketId("LT"));

        return new ModelAndView("infusionsoft/ui/registration/welcome", model);
    }

    /**
     * Registers a new user account.
     */
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String username = request.getParameter("username");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        Map<String, Object> model = new HashMap<String, Object>();
        User user = null;

        try {
            user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password1));
            user.setEnabled(true);

            model.put("user", user);

            if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.put("error", "registration.error.invalidUsername");
            } else if (hibernateTemplate.find("from User u where u.username = ?", username).size() > 0) {
                model.put("error", "registration.error.usernameInUse");
            } else if (password1 == null || password1.length() < PASSWORD_LENGTH_MIN || password1.length() > PASSWORD_LENGTH_MAX) {
                model.put("error", "registration.error.invalidPassword");
            } else if (!password1.equals(password2)) {
                model.put("error", "registration.error.passwordsNoMatch");
            }

            if (model.containsKey("error")) {
                log.warn("couldn't create new user account: " + model.get("error"));
            } else {
                hibernateTemplate.save(user);

                infusionsoftAuthenticationService.createTicketGrantingTicket(username, password1, request, response);
            }
        } catch (Exception e) {
            log.error("failed to create user account", e);

            model.put("error", "registration.error.exception");
        }

        if (model.containsKey("error")) {
            return new ModelAndView("infusionsoft/ui/registration/welcome", model);
        } else if (StringUtils.isNotEmpty((String) request.getSession(true).getAttribute("refererAppName"))) {
            return new ModelAndView("redirect:verification");
        } else {
            return new ModelAndView("redirect:success");
        }
    }

    /**
     * If the user came here from an app, verify that they are already associated to it. If so, send them to the
     * success action. If not, show the form to get their credentials.
     */
    public ModelAndView verification(HttpServletRequest request, HttpServletResponse response) {
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        HttpSession session = request.getSession(true);
        String appName = (String) session.getAttribute("refererAppName");
        String appType = (String) session.getAttribute("refererAppType");

        if (StringUtils.isNotEmpty(appName) && StringUtils.isNotEmpty(appType)) {
            if (infusionsoftAuthenticationService.isUserAssociated(user, appType, appName)) {
                return new ModelAndView("redirect:success");
            } else {
                return new ModelAndView("infusionsoft/ui/registration/verification");
            }
        } else {
            return new ModelAndView("redirect:success");
        }
    }

    public ModelAndView verify(HttpServletRequest request, HttpServletResponse response) {
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        HttpSession session = request.getSession(true);
        String appName = (String) session.getAttribute("refererAppName");
        String appType = (String) session.getAttribute("refererAppType");
        String appUsername = request.getParameter("appUsername");
        String appPassword = request.getParameter("appPassword");

        if (infusionsoftAuthenticationService.verifyAppCredentials(appType, appName, appUsername, appPassword)) {
            infusionsoftAuthenticationService.associateAccountToUser(user, appType, appName, appUsername);

            return new ModelAndView("redirect:success");
        } else {
            Map<String, Object> model = new HashMap<String, Object>();

            model.put("appUsername", appUsername);
            model.put("error", "registration.error.invalidLegacyCredentials");

            return new ModelAndView("infusionsoft/ui/registration/verification", model);
        }
    }

    /**
     * Shows the registration success page.
     */
    public ModelAndView success(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = infusionsoftAuthenticationService.getCurrentUser(request);

        if (user == null) {
            return new ModelAndView("redirect:welcome");
        } else {
            model.put("user", user);

            return new ModelAndView("infusionsoft/ui/registration/success", model);
        }
    }

    /**
     * Shows the "forgot password" dialog.
     */
    public ModelAndView forgot(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("infusionsoft/ui/registration/forgot");
    }

    /**
     * Shows the password recovery dialog.
     */
    public ModelAndView recover(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter("username");
        String recoveryCode = request.getParameter("recoveryCode");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");

        log.info("password recovery request for email " + email);

        if (StringUtils.isNotEmpty(recoveryCode)) {
            User user = infusionsoftAuthenticationService.findUserByRecoveryCode(recoveryCode);

            if (user == null) {
                log.warn("invalid password recovery code was entered: " + recoveryCode);

                return new ModelAndView("infusionsoft/ui/registration/recover", "error", "forgotpassword.noSuchCode");
            } else {
                log.info("correct password recovery code was entered for user " + user.getId());

                return new ModelAndView("infusionsoft/ui/registration/reset", "recoveryCode", recoveryCode);
            }
        } else if (StringUtils.isNotEmpty(email)) {
            List<User> users = (List<User>) hibernateTemplate.find("from User where username = ?", email);

            if (users.size() > 0) {
                recoveryCode = infusionsoftAuthenticationService.createPasswordRecoveryCode(users.get(0));

                log.info("password recovery code " + recoveryCode + " created for user " + users.get(0).getId());

                infusionsoftMailService.sendPasswordResetEmail(users.get(0));

                return new ModelAndView("infusionsoft/ui/registration/recover", "recoveryCode", recoveryCode);
            } else {
                log.warn("password recovery attempted for non-existent user: " + email);

                return new ModelAndView("infusionsoft/ui/registration/forgot", "error", "forgotpassword.noSuchUser");
            }
        } else {
            return new ModelAndView("infusionsoft/ui/registration/forgot");
        }
    }

    public ModelAndView reset(HttpServletRequest request, HttpServletResponse response) {
        String recoveryCode = request.getParameter("recoveryCode");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        Map<String, Object> model = new HashMap<String, Object>();
        User user = infusionsoftAuthenticationService.findUserByRecoveryCode(recoveryCode);

        if (user == null) {
            model.put("error", "forgotpassword.noSuchCode");
        } else if (password1 == null || password1.length() < PASSWORD_LENGTH_MIN || password1.length() > PASSWORD_LENGTH_MAX) {
            model.put("error", "registration.error.invalidPassword");
        } else if (!password1.equals(password2)) {
            model.put("error", "registration.error.passwordsNoMatch");
        }

        if (model.containsKey("error")) {
            model.put("recoveryCode", recoveryCode);

            return new ModelAndView("infusionsoft/ui/registration/reset", model);
        } else {
            user.setPassword(passwordEncoder.encode(password1));

            hibernateTemplate.update(user);

            log.info("reset password for user " + user.getId() + " to " + user.getPassword());

            return new ModelAndView("redirect:/login");
        }
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

    public void setInfusionsoftAuthenticationService(InfusionsoftAuthenticationService infusionsoftAuthenticationService) {
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
    }

    public void setInfusionsoftMailService(InfusionsoftMailService infusionsoftMailService) {
        this.infusionsoftMailService = infusionsoftMailService;
    }
}
