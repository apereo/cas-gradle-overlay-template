package com.infusionsoft.cas.web;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.types.AppType;
import com.infusionsoft.cas.types.User;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Filter that sets various useful things in the session. We need this because CAS uses its own bowels for
 * user authentication, and it's too big a pain to hook into them. This filter also keeps track of a registration
 * code if one is sent on the initial request.
 */
public class UserFilter implements Filter {
    private static final Logger log = Logger.getLogger(UserFilter.class);

    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private String contextPath = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        contextPath = filterConfig.getServletContext().getContextPath();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Don't do anything for the RestController.
        // TODO - dumb to do it this way but filter-mapping expressions are so clumsy
        if (request.getContextPath().startsWith("/rest")) {
            filterChain.doFilter(request, response);

            return;
        }

        HttpSession session = request.getSession(true);
        User user = infusionsoftAuthenticationService.getCurrentUser(request);

        if (user != null) {
            request.getSession(true).setAttribute("user", user);
        } else {
            request.getSession(true).removeAttribute("user");
        }

        storeRegistrationCodeInSession(request, response);
        storeMigrationDateInSession(request, response);
        interpretServiceUrl(request);

        // Clear a stored service from the session on logout.
        if (request.getServletPath().startsWith("/logout")) {
            session.removeAttribute("serviceUrl");
            session.removeAttribute("refererUrl");
            session.removeAttribute("refererAppName");
            session.removeAttribute("refererAppType");
        }

        // Protect the central controller, logged in users only!
        if (session.getAttribute("user") == null && request.getServletPath().startsWith("/central")) {
            response.sendRedirect(contextPath + "/login");
        } else {
            filterChain.doFilter(request, response);
        }
    }

    public void destroy() {
    }

    public void setInfusionsoftAuthenticationService(InfusionsoftAuthenticationService infusionsoftAuthenticationService) {
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
    }

    /**
     * If there's a registration code, store it in the session so we have it later.
     */
    private void storeRegistrationCodeInSession(HttpServletRequest request, HttpServletResponse response) {
        String registrationCode = request.getParameter("registrationCode");

        if (StringUtils.isNotEmpty(registrationCode)) {
            request.getSession(true).setAttribute("registrationCode", registrationCode);

            log.debug("registration code " + registrationCode + " has been saved in the session");
        }
    }

    /**
     * If we've configured a migration date, put it in the session so we can display a countdown.
     * It's so stupid to use the session for this, but need to get around stupid WebFlow.
     */
    private void storeMigrationDateInSession(HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isNotEmpty(infusionsoftAuthenticationService.getMigrationDateString())) {
            try {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date migrationDate = format.parse(infusionsoftAuthenticationService.getMigrationDateString());
                long timeToMigrate = migrationDate.getTime() - System.currentTimeMillis();
                long daysToMigrate = Math.round(timeToMigrate / 86400000);

                request.getSession(true).setAttribute("migrationDate", migrationDate);

                if (daysToMigrate > 0) {
                    request.getSession(true).setAttribute("daysToMigrate", daysToMigrate);
                }
            } catch (Exception e) {
                log.warn("unable to parse migration date: " + infusionsoftAuthenticationService.getMigrationDateString());
            }
        }
    }

    /**
     * If there's a service URL, use it to guess the app type and app name they are trying to reach,
     * and whether or not they've already associated it to their Infusionsoft ID.
     */
    private void interpretServiceUrl(HttpServletRequest request) {
        String service = request.getParameter("service");

        if (StringUtils.isNotEmpty(service)) {
            request.getSession(true).setAttribute("serviceUrl", service);

            log.debug("service url " + service + " has been saved in the session");

            try {
                URL serviceUrl = new URL(service);
                String appName = infusionsoftAuthenticationService.guessAppName(serviceUrl);
                String appType = infusionsoftAuthenticationService.guessAppType(serviceUrl);

                if (StringUtils.equals(appType, AppType.CRM)) {
                    String refererUrl = infusionsoftAuthenticationService.buildAppUrl(appType, appName);

                    request.getSession().setAttribute("refererUrl", refererUrl);
                    request.getSession().setAttribute("refererAppName", appName);
                    request.getSession().setAttribute("refererAppType", appType);

                    log.debug("stored referer app info in session: " + appName + "/" + appType);
                }

                if (StringUtils.isNotEmpty(appName) && StringUtils.isNotEmpty(appType)) {
                    request.setAttribute("appMigrated", infusionsoftAuthenticationService.isAppMigrated(appName, appType));
                }
            } catch (Exception e) {
                log.warn("couldn't parse and interpret service url: " + service, e);
            }
        }
    }
}
