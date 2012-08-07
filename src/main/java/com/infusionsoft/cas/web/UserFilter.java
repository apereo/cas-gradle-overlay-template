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
import java.net.URL;

/**
 * Filter that sets the user object in the session, if available. We need this because CAS uses its own bowels for
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
