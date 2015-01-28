package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.services.CasRegisteredServiceService;
import org.jasig.cas.services.RegisteredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorsPreflightFilter extends OncePerRequestFilter {

    @Autowired
    private CasRegisteredServiceService registeredServiceService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equals(request.getMethod()) && request.getHeader("Access-Control-Request-Method") != null) {
            String originHeader = request.getHeader("Origin");

            RegisteredService registeredService = registeredServiceService.getEnabledRegisteredServiceByUrl(originHeader);
            if (registeredService != null) {
                // A registered service was found, so accept that origin
                response.setHeader("Access-Control-Allow-Origin", originHeader);
                response.setHeader("Access-Control-Allow-Methods", "POST");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authentication");
                response.setHeader("Access-Control-Max-Age", "300"); // 5 minutes
            }

        }

        filterChain.doFilter(request, response);
    }
}
