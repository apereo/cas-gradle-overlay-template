package com.infusionsoft.cas.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class InfusionsoftLogoutSuccessHandler implements LogoutSuccessHandler {

    @Value("${server.prefix}")
    String serverPrefix;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String queryString = request.getQueryString();
        response.sendRedirect(serverPrefix + "/logout" + (StringUtils.isNotEmpty(queryString) ? "?" + queryString : ""));
    }
}
