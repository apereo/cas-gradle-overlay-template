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
 * user authentication, and it's too big a pain to hook into them.
 */
public class UserFilter implements Filter {
    private static final Logger log = Logger.getLogger(UserFilter.class);

    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private String crmDomain;
    private String communityDomain;
    private String customerHubDomain;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Attempts to set a few important session attributes (user, refererAppName, refererAppType).
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(true);
        User user = infusionsoftAuthenticationService.getCurrentUser(request);

        if (user != null) {
            request.getSession(true).setAttribute("user", user);
        } else {
            request.getSession(true).removeAttribute("user");
        }

        if (session.getAttribute("refererAppName") == null) {
            String referer = request.getParameter("service");

            if (StringUtils.isNotEmpty(referer)) {
                log.info("parsing referer: " + referer);

                try {
                    URL refererUrl = new URL(referer);
                    String refererHost = refererUrl.getHost().toLowerCase();
                    String refererAppName = "";
                    String refererAppType = "";

                    log.debug("crm domain is " + crmDomain);
                    log.debug("community domain is " + communityDomain);
                    log.debug("customerhub domain is " + customerHubDomain);

                    if (refererHost.equals(communityDomain)) {
                        refererAppName = "community";
                        refererAppType = "community";
                    } else if (refererHost.endsWith("." + crmDomain)) {
                        refererAppName = refererHost.replace("." + crmDomain, "");
                        refererAppType = "crm";
                    } else if (refererHost.endsWith("." + customerHubDomain)) {
                        refererAppName = refererHost.replace("." + customerHubDomain, "");
                        refererAppType = "customerhub";
                    }

                    log.info("setting refererAppName to " + refererAppName);
                    log.info("setting refererAppType to " + refererAppType);

                    session.setAttribute("refererAppName", refererAppName);
                    session.setAttribute("refererAppType", refererAppType);
                } catch (Exception e) {
                    log.warn("unable to parse referer: " + referer, e);
                }
            }
        }

        filterChain.doFilter(request, response);
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
