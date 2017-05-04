package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.domain.OAuthServiceConfig;
import org.apereo.cas.infusionsoft.oauth.exceptions.OAuthException;
import org.apereo.cas.infusionsoft.oauth.exceptions.OAuthInvalidRequestException;
import org.apereo.cas.infusionsoft.oauth.exceptions.OAuthServerErrorException;
import org.apereo.cas.infusionsoft.services.OAuthServiceConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class OAuthAbstractAuthenticationFilter extends GenericFilterBean {

    private static final String credentialsCharset = "UTF-8";
    private static final Pattern LEGACY_TOKEN_ENDPOINT_SERVICE_KEY_PATTERN = Pattern.compile("/app/oauth/service/([^/]+)/token");

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @Autowired
    @Qualifier("oauthAuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private OAuthAuthenticationEntryPoint oAuthAuthenticationEntryPoint;

    @Autowired
    private OAuthServiceConfigService oAuthServiceConfigService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;

        try {
            OAuthAuthenticationToken authenticationToken = createAuthenticationToken(request, response);
            if (authenticationToken == null) {
                chain.doFilter(request, response);
                return;
            }

            authenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
            Authentication authResult = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authResult);
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();
            oAuthAuthenticationEntryPoint.commence(request, response, failed);
            return;
        }

        chain.doFilter(request, response);
    }

    private OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String grantType = StringUtils.defaultString(request.getParameter("grant_type"));
        String serviceName = StringUtils.defaultString(request.getParameter("service_name"));
        String clientId = StringUtils.defaultString(request.getParameter("client_id"));
        String clientSecret = StringUtils.defaultString(request.getParameter("client_secret"));
        String passedScopeUnSplit = StringUtils.defaultString(request.getParameter("scope"));
        String[] passedScope = StringUtils.split(passedScopeUnSplit, "|");

        OAuthServiceConfig oAuthServiceConfig = oAuthServiceConfigService.loadOAuthServiceConfig(serviceName);

        String scope = null;
        String application = null;
        if (oAuthServiceConfig != null && !StringUtils.equals(serviceName, "crm")) {
            // For all apps but CRM, pass the scope through unchanged and don't set the app
            scope = passedScopeUnSplit;
            application = "";
        } else {
            // For CRM apps, the scope includes the app name, so parse it out
            if (passedScope.length > 1) {
                scope = passedScope[0];
                application = passedScope[1];
            } else if (passedScope.length == 1) {
                scope = "";
                application = passedScope[0];
            }
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Basic ")) {
            String[] clientCredentials = extractAndDecodeHeader(header);
            clientId = clientCredentials[0];
            clientSecret = clientCredentials[1];
        }

        if (StringUtils.isBlank(grantType)) {
            throw new OAuthInvalidRequestException("oauth.exception.grantType.missing");
        }

        if (oAuthServiceConfig == null) {
            // Take this out when the legacy token endpoint is removed
            final Matcher matcher = LEGACY_TOKEN_ENDPOINT_SERVICE_KEY_PATTERN.matcher(request.getRequestURI());
            String legacyServiceKey = matcher.matches() && matcher.groupCount() == 1 ? matcher.group(1) : "";
            if (StringUtils.isNotBlank(legacyServiceKey)) {
                oAuthServiceConfig = new OAuthServiceConfig();
                oAuthServiceConfig.setServiceKey(legacyServiceKey);
            }
        }
        if (oAuthServiceConfig == null) {
            throw new OAuthInvalidRequestException("oauth.exception.service.missing");
        }

        try {
            return createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);
        } catch (OAuthException e) {
            throw e;
        } catch (Exception e) {
            throw new OAuthServerErrorException(e);
        }
    }

    protected abstract OAuthAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response, String scope, String application, String grantType, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret);

    /**
     * Decodes the header into a username and password. Copied from BasicAuthenticationFilter.
     *
     * @throws OAuthInvalidRequestException if the Basic header is not present or is not valid Base64
     */
    private String[] extractAndDecodeHeader(String header) throws IOException {

        byte[] base64Token = header.substring(6).getBytes(credentialsCharset);
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new OAuthInvalidRequestException("oauth.exception.clientId.bad");
        }

        String token = new String(decoded, credentialsCharset);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new OAuthInvalidRequestException("oauth.exception.clientSecret.bad");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }
}
