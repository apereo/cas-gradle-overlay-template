package com.infusionsoft.cas.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class OAuthAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    OAuthExceptionHandler oAuthExceptionHandler;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        ModelAndView modelAndView = oAuthExceptionHandler.doResolveException(request, response, null, e);
        OutputStream outputStream = response.getOutputStream();
        objectMapper.writeValue(outputStream, modelAndView.getModel());
    }
}
