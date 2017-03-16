package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.exceptions.OAuthException;
import com.infusionsoft.cas.oauth.exceptions.OAuthServerErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthExceptionHandler extends AbstractHandlerExceptionResolver {

    private final static String ERROR_PARAMETER = "error";
    private final static String ERROR__DESCRIPTION_PARAMETER = "error_description";
    private final static String ERROR__URI_PARAMETER = "error_uri";
    private final static String REDIRECT_URI_PARAMETER = "redirect_uri";
    private final static String STATE_PARAMETER = "state";

    private Logger logger = LoggerFactory.getLogger(OAuthExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, String> model = new HashMap<>();

        OAuthException oAuthException;
        if (e instanceof OAuthException) {
            oAuthException = (OAuthException) e;
        } else {
            oAuthException = new OAuthServerErrorException(e);
        }

        logger.error("Unhandled OAuthException", e);

        model.put(ERROR_PARAMETER, oAuthException.getErrorCode());

        if (StringUtils.isNotBlank(oAuthException.getErrorDescription())) {
            String message;
            try {
                message = messageSource.getMessage(oAuthException.getErrorDescription(), null, LocaleContextHolder.getLocale());
            } catch (NoSuchMessageException messageException) {
                message = oAuthException.getErrorDescription();
            }

            model.put(ERROR__DESCRIPTION_PARAMETER, message);
        }

        if (StringUtils.isNotBlank(oAuthException.getErrorUri())) {
            model.put(ERROR__URI_PARAMETER, oAuthException.getErrorUri());
        }

        // Errors from authorization code generation are returned via query parameters appended to the redirect URI
        // See https://tools.ietf.org/html/rfc6749#section-4.1.2.1
        if (StringUtils.equals(request.getPathInfo(), "/oauth/processAuthorization")) {
            model.put(STATE_PARAMETER, request.getParameter(STATE_PARAMETER));

            final String redirectUri = request.getParameter(REDIRECT_URI_PARAMETER);
            if (StringUtils.isBlank(redirectUri)) {
                // If there's no redirect URI we have nowhere to redirect to, so just show a text error message
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("text/plain");
                modelAndView.setView(new View() {
                    @Override
                    public String getContentType() {
                        return "text/plain";
                    }

                    @Override
                    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                        response.getWriter().write("Missing redirect_uri");
                    }
                });
            } else {
                RedirectView redirectView = new RedirectView(redirectUri);
                redirectView.setStatusCode(HttpStatus.FOUND);
                redirectView.setExposeModelAttributes(true);
                modelAndView.setView(redirectView);
            }
        } else {
            response.setStatus(oAuthException.getHttpStatus().value());
            modelAndView.setView(new MappingJackson2JsonView());
        }

        modelAndView.addAllObjects(model);

        return modelAndView;
    }
}
