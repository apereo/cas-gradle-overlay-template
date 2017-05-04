package org.apereo.cas.infusionsoft.authentication;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Authentication filter that gets the API key in one of two places.  First it looks in the basic auth header
 * where the username is "X-Infusionsoft-API-Key".  If that exists, the password is used as the API key.  Otherwise,
 * falls back to looking in the custom header X-Infusionsoft-API-Key. If the API key was found in the basic auth header,
 * the global user ID is retrieved from the X-Mashery-Oauth-User-Context header.  If the API key was found
 * in the X-Infusionsoft-API-Key header, then the global user ID is retrieved from X-Mashery-Oauth-User-Context if it
 * exists, otherwise X-Infusionsoft-Global-User-ID. If no global user ID is found, the result is an anonymous login.
 */
@Component
public class ApiAuthenticationFilter extends GenericFilterBean {

    @Autowired
    private ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

    @Autowired
    private AuthenticationManager apiAuthenticationManager;

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String MASHERY_OAUTH_USER_CONTEXT = "X-Mashery-Oauth-User-Context";

    public void afterPropertiesSet() {
        Assert.notNull(apiAuthenticationManager, "An AuthenticationManager is required");
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        final boolean debug = logger.isDebugEnabled();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (debug) {
                logger.debug("Already authenticated as " + authentication.getPrincipal() + ", skipping API authentication filter");
            }
        } else {
            final HttpServletRequest request = (HttpServletRequest) req;
            final HttpServletResponse response = (HttpServletResponse) resp;
            final String apiKeyFromBasicAuth = getBasicAuthCredentials(request);
            String apiKey = StringUtils.defaultIfBlank(apiKeyFromBasicAuth, request.getHeader("X-Infusionsoft-API-Key"));

            if (StringUtils.isBlank(apiKey)) {
                if (debug) {
                    logger.debug("Missing API key, skipping API authentication filter");
                }
            } else {
                String userContext = null;
                try {
                    userContext = extractUserContext(request);
                    Long globalUserId = extractGlobalUserId(request, userContext);
                    ApiAuthenticationToken token = new ApiAuthenticationToken(apiKey, globalUserId, userContext);
                    try {
                        authentication = apiAuthenticationManager.authenticate(token);
                    } catch (AuthenticationException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new AuthenticationServiceException("Unknown error", e);
                    }
                } catch (AuthenticationException e) {
                    // Authentication failed
                    if (debug) {
                        logger.debug("Authentication request for user " + userContext + " failed", e);
                    }
                    SecurityContextHolder.getContext().setAuthentication(null);
                    apiAuthenticationEntryPoint.commence(request, response, e);
                    return;
                }
                if (authentication != null) {
                    // Authentication success
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    if (debug) {
                        logger.debug("Authentication success: " + authentication);
                    }
                }
            }
        }
        chain.doFilter(req, resp);
    }

    private String getBasicAuthCredentials(HttpServletRequest request) {
        String apiKey = null;
        String header = request.getHeader("Authorization");

        if ((header != null) && header.startsWith("Basic ")) {
            byte[] base64Token = header.substring(6).getBytes(UTF8);
            String token;
            try {
                token = new String(Base64.decode(base64Token), UTF8);
            } catch (IllegalArgumentException ignored) {
                token = "";
            }

            int delim = token.indexOf(":");
            if (delim != -1) {
                String username = token.substring(0, delim);
                String password = token.substring(delim + 1);
                if (logger.isDebugEnabled()) {
                    logger.debug("Basic Auth header found for user '" + username + "'");
                }
                if ("X-Infusionsoft-API-Key".equals(username)) {
                    apiKey = password;
                }
            }
        }
        return apiKey;
    }

    private String extractUserContext(HttpServletRequest request) {
        String userContext = request.getHeader(MASHERY_OAUTH_USER_CONTEXT);
        if (userContext != null) {
            String[] splitResult = StringUtils.split(userContext, "|");
            if (splitResult != null && splitResult.length > 0 && NumberUtils.isNumber(splitResult[0])) {
                userContext = splitResult[0];
            } else {
                userContext = null;
            }
        }
        return userContext;
    }

    private Long extractGlobalUserId(HttpServletRequest request, String userContext) {
        // global id from Mashery takes precedence over X-Infusionsoft-Global-User-ID if both exist
        if (userContext == null) {
            userContext = request.getHeader("X-Infusionsoft-Global-User-ID");
        } else if (StringUtils.startsWith(userContext, "anonymous-")) {
            return null;
        }
        Long globalUserId;
        try {
            globalUserId = userContext == null ? null : Long.parseLong(userContext, 10);
        } catch (NumberFormatException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid format for Global UserID: " + userContext + " ", e);
            }
            throw new BadCredentialsException("Malformed Global User ID", e);
        }
        return globalUserId;
    }

}
