package com.infusionsoft.cas.web;

import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<String, Object>();
        User user = infusionsoftAuthenticationService.getCurrentUser(request);

        System.out.println("****** in /home, user is " + user);

        if (user != null) {
            model.put("user", user);

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

    public ModelAndView associate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<String, Object>();

        try {
            User currentUser = infusionsoftAuthenticationService.getCurrentUser(request);
            String appType = request.getParameter("appType");
            String appName = request.getParameter("appName");
            String appUsername = request.getParameter("appUsername");
            String appPassword = request.getParameter("appPassword");

            if (currentUser != null) {
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

                JSONObject returnValue =(JSONObject)JSONValue.parse(result);

                Boolean isValidUser = (Boolean)returnValue.get("valid");

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

                    JSONObject returnValue =(JSONObject)JSONValue.parse(result);

                    Boolean hasError = (Boolean)returnValue.get("error");

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

    public void setInfusionsoftAuthenticationService(InfusionsoftAuthenticationService infusionsoftAuthenticationService) {
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
    }
}
