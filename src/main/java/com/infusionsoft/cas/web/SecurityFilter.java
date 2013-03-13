package com.infusionsoft.cas.web;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that sets headers for security purposes, mostly to get around issues identified by OWASP ZAP.
 */
public class SecurityFilter implements Filter {
    private static final Logger log = Logger.getLogger(SecurityFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getServletPath();

        // The banner gets framed by CRM apps so it needs to allow framing; all others forbid it
        if (!request.getRequestURI().contains("registration/banner")) {
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
        }

        // Prevent content-type sniffing by certain browsers
        response.setHeader("X-Content-Type-Options", "nosniff");

        filterChain.doFilter(request, response);

        // Forcibly set no-cache headers
        if (path.startsWith("/login") || path.startsWith("/logout")) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
            response.setHeader("Pragma", "no-cache");
        }
    }

    public void destroy() {
    }
}
