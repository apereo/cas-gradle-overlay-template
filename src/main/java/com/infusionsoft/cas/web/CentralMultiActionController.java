package com.infusionsoft.cas.web;

import com.infusionsoft.cas.exceptions.UsernameTakenException;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.InfusionsoftMailService;
import com.infusionsoft.cas.types.CommunityAccountDetails;
import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller that powers the central "hub" and association features.
 */
public class CentralMultiActionController extends MultiActionController {
    private static final Logger log = Logger.getLogger(CentralMultiActionController.class);

    private static final int PASSWORD_LENGTH_MIN = 7;
    private static final int PASSWORD_LENGTH_MAX = 20;
    private static final String FORUM_API_KEY = "bec0124123e5ab4c2ce362461cb46ff0";

    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private InfusionsoftMailService infusionsoftMailService;
    private HibernateTemplate hibernateTemplate;
    private PasswordEncoder passwordEncoder;

    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        HttpSession session = request.getSession(true);
        String appType = (String) session.getAttribute("refererAppType");
        String appName = (String) session.getAttribute("refererAppName");

        if (StringUtils.isNotEmpty(appType) && StringUtils.isNotEmpty(appName)) {
            if (infusionsoftAuthenticationService.isUserAssociated(user, appType, appName)) {
                String appUrl = infusionsoftAuthenticationService.buildAppUrl(appType, appName);

                log.info("redirecting user " + user.getId() + " to " + appUrl);

                response.sendRedirect(appUrl);

                return null;
            } else {
                log.warn("user was referred from an unassociated app: " + appName + ", " + appType);

                // TODO - do we want to force them to complete the association here?

                return new ModelAndView("redirect:home");
            }
        } else {
            return new ModelAndView("redirect:home");
        }
    }

    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = infusionsoftAuthenticationService.getCurrentUser(request);

        if (user != null) {
            model.put("user", user);
            model.put("homeLinkSelected", "selected");
            model.put("hasCommunityAccount", infusionsoftAuthenticationService.hasCommunityAccount(user));
            model.put("accounts", infusionsoftAuthenticationService.getSortedUserAccounts(user));

            return new ModelAndView("infusionsoft/ui/central/home", model);
        } else {
            model.put("service", request.getContextPath() + "/login");

            return new ModelAndView("redirect:/logout", model);
        }
    }

    public ModelAndView linkInfusionsoftAppAccount(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("infusionsoft/ui/central/linkInfusionsoftAppAccount");
    }

    public ModelAndView linkCustomerHubAccount(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("infusionsoft/ui/central/linkCustomerHubAccount");
    }

    public ModelAndView linkCommunityAccount(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("infusionsoft/ui/central/linkCommunityAccount");
    }

    public ModelAndView createCommunityAccount(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        CommunityAccountDetails details = new CommunityAccountDetails();

        model.put("infusionsoftExperienceLevels", new int[]{1, 2, 3, 4, 5});
        model.put("details", details);

        if (request.getMethod().equalsIgnoreCase("POST")) {
            boolean agreeToRules = Boolean.valueOf(request.getParameter("agreeToRules"));

            details.setDisplayName(request.getParameter("displayName"));
            details.setInfusionsoftExperience(Integer.parseInt(request.getParameter("infusionsoftExperience")));
            details.setTimeZone(request.getParameter("timeZone"));
            details.setNotificationEmailAddress(request.getParameter("notificationEmailAddress"));
            details.setTwitterHandle(request.getParameter("twitterHandle"));

            if (StringUtils.isEmpty(details.getDisplayName()) || details.getDisplayName().length() < 4 || details.getDisplayName().length() > 30) {
                model.put("error", "community.error.displayNameInvalid");
            } else if (StringUtils.isNotEmpty(details.getNotificationEmailAddress()) && !EmailValidator.getInstance().isValid(details.getNotificationEmailAddress())) {
                model.put("error", "community.error.notificationEmailAddressInvalid");
            } else if (!agreeToRules) {
                model.put("error", "community.error.agreeToRules");
            }

            if (!model.containsKey("error")) {
                log.info("attempting to register a forum account for user " + user.getId());

                try {
                    infusionsoftAuthenticationService.registerCommunityUserAccount(user, details);
                    infusionsoftAuthenticationService.createTicketGrantingTicket(user.getUsername(), "bogus", request, response);

                    return new ModelAndView("redirect:index");
                } catch (UsernameTakenException e) {
                    log.error("failed to register community account for user " + user.getId(), e);

                    model.put("error", "community.error.displayNameTaken");
                } catch (Exception e) {
                    log.error("unexpected error while registering community account for user " + user.getId(), e);

                    model.put("error", "community.error.unknown");
                }
            }
        }

        return new ModelAndView("infusionsoft/ui/central/createCommunityAccount", model);
    }

    public ModelAndView associate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();

        try {
            User currentUser = infusionsoftAuthenticationService.getCurrentUser(request);
            String appType = request.getParameter("appType");
            String appName = request.getParameter("appName");
            String appUsername = request.getParameter("appUsername");
            String appPassword = request.getParameter("appPassword");

            if (appType.equals("community")) {
                appName = "community";
            }

            if (StringUtils.isEmpty(appUsername)) {
                model.put("error", "registration.error.invalidUsername");
            } else if (StringUtils.isEmpty(appPassword)) {
                model.put("error", "registration.error.invalidPassword");
            } else if (currentUser != null) {
                // TODO - big security hole here! need to validate they really have access before mapping

                infusionsoftAuthenticationService.associateAccountToUser(currentUser, appType, appName, appUsername);
                infusionsoftAuthenticationService.createTicketGrantingTicket(currentUser.getUsername(), "bogus", request, response);
            } else {
                throw new RuntimeException("logged in user could not be resolved!");
            }
        } catch (Exception e) {
            log.error("failed to associate account", e);

            model.put("error", "registration.error.couldNotAssociate");
        }

        if (model.containsKey("error")) {
            response.sendError(500, "Failed to associate");

            return null;
        } else {
            return new ModelAndView("redirect:home");
        }
    }

    public ModelAndView editProfile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HashMap<String, Object> model = new HashMap<String, Object>();

            model.put("user", infusionsoftAuthenticationService.getCurrentUser(request));
            model.put("editProfileLinkSelected", "selected");

            return new ModelAndView("infusionsoft/ui/central/editProfile", model);
        } catch (Exception e) {
            log.error("unable to load user for current request!", e);

            return new ModelAndView("redirect:home");
        }
    }

    public ModelAndView updateProfile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String username = request.getParameter("username");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        Map<String, Object> model = new HashMap<String, Object>();

        try {
            User user = (User) hibernateTemplate.get(User.class, new Long(request.getParameter("id")));

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);

            if (username == null || username.isEmpty() || !EmailValidator.getInstance().isValid(username)) {
                model.put("error", "editprofile.error.invalidUsername");
            } else if (hibernateTemplate.find("from User u where u.username = ? and u.id != ?", username, user.getId()).size() > 0) {
                model.put("error", "editprofile.error.usernameInUse");
            } else if (StringUtils.isNotEmpty(password1) || StringUtils.isNotEmpty(password2)) {
                user.setPassword(passwordEncoder.encode(password1));

                if (password1.length() < PASSWORD_LENGTH_MIN || password1.length() > PASSWORD_LENGTH_MAX) {
                    model.put("error", "editprofile.error.invalidPassword");
                } else if (!password1.equals(password2)) {
                    model.put("error", "editprofile.error.passwordsNoMatch");
                }
            }

            model.put("user", user);

            if (model.containsKey("error")) {
                log.info("couldn't update user account for user " + user.getId() + ": " + model.get("error"));
            } else {
                hibernateTemplate.update(user);

                infusionsoftAuthenticationService.createTicketGrantingTicket(username, password1, request, response);
            }
        } catch (Exception e) {
            log.error("failed to update user account", e);

            model.put("error", "editprofile.error.exception");
        }

        if (model.containsKey("error")) {
            return new ModelAndView("infusionsoft/ui/central/editProfile", model);
        } else {
            return new ModelAndView("redirect:home");
        }
    }

    public ModelAndView editCommunityAccount(HttpServletRequest request, HttpServletResponse response) {
        Long accountId = new Long(request.getParameter("id"));
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        UserAccount account = infusionsoftAuthenticationService.findUserAccount(user, accountId);
        Map<String, Object> model = new HashMap<String, Object>();

        model.put("user", user);
        model.put("account", account);
        model.put("details", infusionsoftAuthenticationService.findCommunityAccountDetails(account));
        model.put("infusionsoftExperienceLevels", new int[]{1, 2, 3, 4, 5});

        return new ModelAndView("infusionsoft/ui/central/editCommunityAccount", model);
    }

    public ModelAndView updateCommunityAccount(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        UserAccount account = infusionsoftAuthenticationService.findUserAccount(user, new Long(request.getParameter("id")));
        CommunityAccountDetails details = infusionsoftAuthenticationService.findCommunityAccountDetails(account);

        details.setDisplayName(request.getParameter("displayName"));
        details.setInfusionsoftExperience(Integer.parseInt(request.getParameter("infusionsoftExperience")));
        details.setTimeZone(request.getParameter("timeZone"));
        details.setNotificationEmailAddress(request.getParameter("notificationEmailAddress"));
        details.setTwitterHandle(request.getParameter("twitterHandle"));

        if (StringUtils.isEmpty(details.getDisplayName()) || details.getDisplayName().length() < 4 || details.getDisplayName().length() > 30) {
            model.put("error", "community.error.displayNameInvalid");
        } else if (StringUtils.isNotEmpty(details.getNotificationEmailAddress()) && !EmailValidator.getInstance().isValid(details.getNotificationEmailAddress())) {
            model.put("error", "community.error.notificationEmailAddressInvalid");
        }

        model.put("account", account);
        model.put("details", details);

        if (!model.containsKey("error")) {
            log.info("attempting to update a forum account for user " + user.getId());

            try {
                infusionsoftAuthenticationService.updateCommunityUserAccount(user, details);
                infusionsoftAuthenticationService.createTicketGrantingTicket(user.getUsername(), "bogus", request, response);

                return new ModelAndView("redirect:index");
            } catch (UsernameTakenException e) {
                model.put("error", "community.error.displayNameTaken");

                log.error("failed to register community account for user " + user.getId(), e);
            } catch (Exception e) {
                model.put("error", "community.error.unknown");

                log.error("unexpected error while registering community account for user " + user.getId(), e);
            }
        }

        return new ModelAndView("infusionsoft/ui/central/editCommunityAccount", model);
    }

    // TODO - still needed?
    public ModelAndView associateForum(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();

        try {
            User currentUser = infusionsoftAuthenticationService.getCurrentUser(request);

            if (currentUser != null) {
                //get parameter
                String forumUser = request.getParameter("forumUsername");
                String plainTextPassword = request.getParameter("forumPassword");

                //we need an MD5 version of the password
                String md5Password = DigestUtils.md5Hex(plainTextPassword);

                RestTemplate restTemplate = new RestTemplate();
                //TODO: parameterize this?
                String result = restTemplate.getForObject("http://infusionsoft.infusiontest.com/forum/rest.php/user/isvaliduser/{user}/{md5password}?key={apiKey}", String.class, forumUser, md5Password, FORUM_API_KEY);

                System.out.println("REST CALL :: " + result);

                JSONObject returnValue = (JSONObject) JSONValue.parse(result);

                Boolean isValidUser = (Boolean) returnValue.get("valid");

                if (isValidUser) {
                    infusionsoftAuthenticationService.associateAccountToUser(currentUser, "forum", "Infusionsoft Communities", String.valueOf(returnValue.get("username")));
                    infusionsoftAuthenticationService.createTicketGrantingTicket(currentUser.getUsername(), "bogus", request, response);

                    model.put("data", "OK");
                } else {
                    model.put("error", "Invalid User");
                }

            } else {
                model.put("error", "Could not find user!");
                log.error("failed to find a valid user account");
            }

        } catch (Exception e) {
            log.error("failed to associate account", e);
            model.put("error", "registration.error.couldNotAssociate");
        }

        if (model.containsKey("error")) {
            response.sendError(500, "Failed to associate");

            return null;
        } else {
            return new ModelAndView("infusionsoft/ui/central/associate", model);
        }
    }

    // TODO - still needed?
    public ModelAndView createForum(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();
        try {
            User currentUser = infusionsoftAuthenticationService.getCurrentUser(request);
            if (currentUser != null) {
                //get parameter
                String forumUser = request.getParameter("forumUsername");
                String email = request.getParameter("forumEmail");

                if (forumUser != null && email != null) {

                    RestTemplate restTemplate = new RestTemplate();
                    //TODO: parameterize this?
                    String result = restTemplate.getForObject("http://infusionsoft.infusiontest.com/forum/rest.php/user/addnewuser/{username}/{email}?key={apiKey}", String.class, forumUser, email, FORUM_API_KEY);

                    System.out.println("CREATE REST CALL :: " + result);

                    JSONObject returnValue = (JSONObject) JSONValue.parse(result);

                    Boolean hasError = (Boolean) returnValue.get("error");

                    if (hasError) {
                        System.out.println("ERROR! - could not create user: " + returnValue.get("message"));
                        model.put("error", returnValue.get("message"));
                    } else {
                        infusionsoftAuthenticationService.associateAccountToUser(currentUser, "forum", "Infusionsoft Communities", String.valueOf(returnValue.get("username")));
                        infusionsoftAuthenticationService.createTicketGrantingTicket(currentUser.getUsername(), "bogus", request, response);

                        model.put("data", "OK");
                    }
                } else {
                    model.put("error", "Please supply a valid username and email address");
                }
            } else {
                model.put("error", "Could not find user!");
                log.error("failed to find a valid user account");
            }
        } catch (Exception e) {
            log.error("failed to associate account", e);
            model.put("error", "registration.error.couldNotAssociate");
        }

        if (model.containsKey("error")) {
            response.sendError(500, "Failed to associate");
            return null;
        } else {
            return new ModelAndView("infusionsoft/ui/central/associate", model);
        }
    }

    /**
     * Called from the AJAX quick edit to rename an account alias.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String renameAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long accountId = new Long(request.getParameter("id"));
        String alias = request.getParameter("value");
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        UserAccount account = infusionsoftAuthenticationService.findUserAccount(user, accountId);

        try {
            account.setAlias(alias);

            hibernateTemplate.update(account);

            return alias;
        } catch (Exception e) {
            log.error("failed to update alias for account " + accountId, e);

            response.sendError(500);

            return null;
        }
    }

    public void setInfusionsoftAuthenticationService(InfusionsoftAuthenticationService infusionsoftAuthenticationService) {
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setInfusionsoftMailService(InfusionsoftMailService infusionsoftMailService) {
        this.infusionsoftMailService = infusionsoftMailService;
    }
}
