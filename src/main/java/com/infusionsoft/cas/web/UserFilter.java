package com.infusionsoft.cas.web;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.types.User;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
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
    private String crmDomain;
    private String communityDomain;
    private String customerHubDomain;
    private String contextPath = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        contextPath = filterConfig.getServletContext().getContextPath();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(true);
        User user = infusionsoftAuthenticationService.getCurrentUser(request);
        String registrationCode = request.getParameter("registrationCode");
        String service = request.getParameter("service");

        // Set or unset the user object (since the CAS security layer doesn't make this easy).
        if (user != null) {
            request.getSession(true).setAttribute("user", user);
        } else {
            request.getSession(true).removeAttribute("user");
        }

        // If there's a registration code, use it to map their account.
        if (StringUtils.isNotEmpty(registrationCode)) {
            request.getSession(true).setAttribute("registrationCode", registrationCode);

            log.debug("registration code " + registrationCode + " has been saved in the session");
        }

        // If there's a migration date, put it in the session so we can display a countdown.
        // TODO - so stupid to use the session for this, but need to get around stupid WebFlow
        if (StringUtils.isNotEmpty(infusionsoftAuthenticationService.getMigrationDateString())) {
            try {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date migrationDate = format.parse(infusionsoftAuthenticationService.getMigrationDateString());
                long timeToMigrate = migrationDate.getTime() - System.currentTimeMillis();
                long daysToMigrate = Math.round(timeToMigrate / 86400000);

                if (daysToMigrate > 0) {
                    request.getSession(true).setAttribute("daysToMigrate", daysToMigrate);
                }
            } catch (Exception e) {
                log.warn("unable to parse migration date: " + infusionsoftAuthenticationService.getMigrationDateString());
            }
        }

        // If there's a service url, save that in the session too.
        if (StringUtils.isNotEmpty(service)) {
            request.getSession(true).setAttribute("serviceUrl", service);

            log.debug("service url " + service + " has been saved in the session");

            try {
                URL serviceUrl = new URL(service);
                String appName = infusionsoftAuthenticationService.guessAppName(serviceUrl);
                String appType = infusionsoftAuthenticationService.guessAppType(serviceUrl);

                if (StringUtils.equals(appType, "crm")) {
                    String refererUrl = serviceUrl.getProtocol() + "://" + serviceUrl.getHost() + ":" + serviceUrl.getPort();

                    request.getSession().setAttribute("refererUrl", refererUrl);
                }

                if (StringUtils.isNotEmpty(appName) && StringUtils.isNotEmpty(appType)) {
                    request.setAttribute("appMigrated", infusionsoftAuthenticationService.isAppMigrated(appName, appType));

                    log.debug("set appMigrated = " + request.getAttribute("appMigrated"));
                }
            } catch (Exception e) {
                log.warn("couldn't parse and interpret service url: " + service, e);
            }
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

    public void setCrmDomain(String crmDomain) {
        this.crmDomain = crmDomain;
    }

    public void setCommunityDomain(String communityDomain) {
        this.communityDomain = communityDomain;
    }

    public void setCustomerHubDomain(String customerHubDomain) {
        this.customerHubDomain = customerHubDomain;
    }
}
