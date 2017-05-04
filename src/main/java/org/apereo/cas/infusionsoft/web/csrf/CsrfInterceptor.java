package org.apereo.cas.infusionsoft.web.csrf;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Custom interceptor that checks for a valid CSRF token on all POST requests.
 */
public class CsrfInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = Logger.getLogger(CsrfInterceptor.class);

    @Value("${csrf.token.name}")
    private String tokenName;

    @Autowired
    private CsrfTokenManager csrfTokenManager;

    /**
     * Intercepts inbound requests and check the CSRF token.
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getMethod().equalsIgnoreCase("POST") ) {
            return true;
        } else {
            String requestToken = request.getParameter(tokenName);
            String expectedToken = csrfTokenManager.getExpectedCsrfTokenForRequest(request);

            // If there's no expected token, CSRF is disabled or the user is not logged in
            if (StringUtils.isEmpty(expectedToken)) {
                return true;
            }

            // Validate the CSRF token
            if (StringUtils.equals(expectedToken, requestToken)) {
                log.debug("CSRF token matches expected token");

                return true;
            } else {
                log.warn("CSRF tokens don't match! expected " + expectedToken + ", got " + requestToken);

                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing CSRF value");

                return false;
            }
        }
    }
}
