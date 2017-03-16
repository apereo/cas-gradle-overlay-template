package com.infusionsoft.cas.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OAuthAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private OAuthExceptionHandler oAuthExceptionHandler;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx) throws IOException, ServletException {
        ModelAndView modelAndView = oAuthExceptionHandler.resolveException(request, response, null, authEx);

        ensureFlashMapManagerExists(request);
        try {
            modelAndView.getView().render(modelAndView.getModel(), request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Workaround for {@link org.springframework.web.servlet.view.RedirectView} always expecting a
     * {@link FlashMapManager} to exist in the request
     */
    private void ensureFlashMapManagerExists(HttpServletRequest request) {
        FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
        if (flashMapManager == null) {
            flashMapManager = new FlashMapManager() {
                @Override
                public FlashMap retrieveAndUpdate(HttpServletRequest request, HttpServletResponse response) {
                    return null;
                }

                @Override
                public void saveOutputFlashMap(FlashMap flashMap, HttpServletRequest request, HttpServletResponse response) {

                }
            };
            request.setAttribute(DispatcherServlet.FLASH_MAP_MANAGER_ATTRIBUTE, flashMapManager);
        }
    }

}
