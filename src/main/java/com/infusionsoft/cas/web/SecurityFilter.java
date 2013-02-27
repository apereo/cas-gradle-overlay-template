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

        if (path.startsWith("/login") || path.startsWith("/logout")) {
            log.debug("adding no-cache headers to " + path);

            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
            response.setHeader("Pragma", "no-cache");
        }

        if (!path.startsWith("/registration/banner")) {
            log.debug("adding no-frame headers to " + path);

            response.setHeader("X-Frame-Options", "SAMEORIGIN");
        }

        log.debug("adding no-sniff headers to " + path);

        response.setHeader("X-Content-Type-Options", "nosniff");

        filterChain.doFilter(request, response);
    }

    public void destroy() {
    }
}
