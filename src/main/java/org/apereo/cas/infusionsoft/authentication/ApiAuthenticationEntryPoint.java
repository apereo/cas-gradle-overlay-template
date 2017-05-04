package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.api.APIErrorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom entry point for our custom header API authentication.
 * Modeled after {@link org.springframework.security.web.authentication.Http403ForbiddenEntryPoint}
 * but writes the error using the {@link APIErrorDTO}.
 */
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter;

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final APIErrorDTO errorDTO = new APIErrorDTO("cas.exception.forbidden", messageSource, new Object[] {e.getMessage()}, request.getLocale());
        HttpOutputMessage outputMessage = new ServletServerHttpResponse(response);
        mappingJacksonHttpMessageConverter.write(errorDTO, MediaType.APPLICATION_JSON, outputMessage);
    }

}
