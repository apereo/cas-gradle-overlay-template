package com.infusionsoft.cas.web;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.types.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that sets the user object in the session, if available. We need this because CAS uses its own bowels for
 * user authentication, and it's too big a pain to hook into them.
 */
public class UserFilter implements Filter {
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        User user = infusionsoftAuthenticationService.getCurrentUser(request);

        if (user != null) {
            request.getSession(true).setAttribute("user", user);
        } else {
            request.getSession(true).removeAttribute("user");
        }

        filterChain.doFilter(request, response);
    }

    public void destroy() {
    }

    public void setInfusionsoftAuthenticationService(InfusionsoftAuthenticationService infusionsoftAuthenticationService) {
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
    }
}
