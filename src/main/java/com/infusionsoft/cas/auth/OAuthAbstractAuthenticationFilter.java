package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.services.OAuthServiceConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class OAuthAbstractAuthenticationFilter extends GenericFilterBean {

    private static final String credentialsCharset = "UTF-8";

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @Autowired
    @Qualifier("casAuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private OAuthServiceConfigService oAuthServiceConfigService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;

        OAuthAuthenticationToken authenticationToken = createAuthenticationToken(request, (HttpServletResponse) response);
        if (authenticationToken == null) {
            chain.doFilter(request, response);
            return;
        }

        authenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
        Authentication authResult = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authResult);

        chain.doFilter(request, response);
    }

    private OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String grantType = StringUtils.defaultString(request.getParameter("grant_type"));
        String serviceName = StringUtils.defaultString(request.getParameter("service_name"));
        String clientId = StringUtils.defaultString(request.getParameter("client_id"));
        String clientSecret = StringUtils.defaultString(request.getParameter("client_secret"));
        String passedScopeUnSplit = StringUtils.defaultString(request.getParameter("scope"));
        String[] passedScope = StringUtils.split(passedScopeUnSplit, "|");

        String scope = null;
        String application = null;
        if (passedScope.length > 1) {
            scope = passedScope[0];
            application = passedScope[1];
        } else if (passedScope.length == 1) {
            scope = "";
            application = passedScope[0];
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Basic ")) {
            String[] clientCredentials = extractAndDecodeHeader(header);
            clientId = clientCredentials[0];
            clientSecret = clientCredentials[1];
        }

        OAuthServiceConfig oAuthServiceConfig = oAuthServiceConfigService.loadOAuthServiceConfig(serviceName);

        return createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);
    }

    protected abstract OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret);

    /**
     * Decodes the header into a username and password.
     * <p/>
     * Copied from BasicAuthenticationFilter
     *
     * @throws org.springframework.security.authentication.BadCredentialsException if the Basic header is not present or is not valid Base64
     */
    private String[] extractAndDecodeHeader(String header) throws IOException {

        byte[] base64Token = header.substring(6).getBytes(credentialsCharset);
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, credentialsCharset);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }
}
