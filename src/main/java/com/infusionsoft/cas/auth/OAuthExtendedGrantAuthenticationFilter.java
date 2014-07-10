package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.services.OAuthService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A Spring Security Filter that is responsible for extracting client credentials and user credentials
 * to be authenticated via CAS and Mashery.
 * <p/>
 * The filter was original copied from BasicAuthenticationFilter and modified from there.
 */
@Component
public class OAuthExtendedGrantAuthenticationFilter extends OAuthAbstractAuthenticationFilter {

    protected String userContext;
    protected Long globalUserId;

    @Autowired
    protected OAuthService oAuthService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;

        extractRequestData(request);
        userContext = StringUtils.defaultString(request.getParameter("user_context")).trim();
        globalUserId = NumberUtils.createLong(request.getParameter("global_user_id"));

        if (!oAuthService.isExtendedGrantType(grantType) || clientId == null || clientSecret == null) {
            chain.doFilter(request, response);
            return;
        }

        OAuthAuthenticationToken authRequest = new OAuthExtendedGrantAuthenticationToken(userContext, null, clientId, clientSecret, scope, grantType, application, globalUserId);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        Authentication authResult = authenticationManager.authenticate(authRequest);

        SecurityContextHolder.getContext().setAuthentication(authResult);

        chain.doFilter(request, response);
    }
}
