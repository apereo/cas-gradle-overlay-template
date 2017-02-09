package org.apereo.cas.infusionsoft.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    /**
     * Locates the cookie in the request and returns it.
     * The cookie is searched for by name.
     *
     * @param request the submitted request which is to be authenticated
     * @return the cookie (if present), null otherwise.
     */
    public static Cookie extractCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if ((cookies == null) || (cookies.length == 0)) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie;
            }
        }

        return null;
    }

    /**
     * Locates the cookie in the request and returns its value.
     * The cookie is searched for by name.
     *
     * @param request the submitted request which is to be authenticated
     * @return the cookie value (if present), null otherwise.
     */
    public static String extractCookieValue(HttpServletRequest request, String cookieName) {
        Cookie cookie = extractCookie(request, cookieName);
        return cookie == null ? null : cookie.getValue();
    }

    public static void setCookie(HttpServletResponse response, String cookieName, String cookieValue, Integer maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        }
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
