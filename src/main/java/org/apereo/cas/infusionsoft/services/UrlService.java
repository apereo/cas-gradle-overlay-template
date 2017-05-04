package org.apereo.cas.infusionsoft.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UrlService {

    public static final String REDIRECT_PREFIX = "redirect:";
    public static final String FORWARD_PREFIX = "forward:";

    public String redirect(String url) {
        return REDIRECT_PREFIX + url;
    }

    public String redirect(String controller, String action, String queryString) {
        return url(REDIRECT_PREFIX, controller, action, null, queryString);
    }

    public String redirect(String controller, String action) {
        return redirect(controller, action, null);
    }

    public String forward(String controller, String action, String queryString) {
        return url(FORWARD_PREFIX, controller, action, null, queryString);
    }

    public String forward(String controller, String action) {
        return forward(controller, action, null);
    }

    public String url(String controller, String action) {
        return url(null, controller, action, null, null);
    }

    public String url(String controller, String action, String extension) {
        return url(null, controller, action, extension, null);
    }

    public String url(String prefix, String controller, String action, String queryString) {
        return url(prefix, controller, action, null, queryString);
    }

    public String url(String prefix, String controller, String action, String extension, String queryString) {
        return url(prefix, controller, action, extension, queryString, null);
    }

    public String url(String prefix, String controller, String action, String extension, String queryString, String id) {
        StringBuilder builder = new StringBuilder();

        if (StringUtils.isNotEmpty(prefix)) {
            builder.append(prefix);
        }

        if (StringUtils.isNotEmpty(controller)) {
            builder.append("/app/").append(controller);
        }

        if (StringUtils.isNotEmpty(action)) {
            builder.append("/").append(action);
        }


        if (StringUtils.isNotEmpty(extension)) {
            builder.append(".").append(extension);
        }

        if (StringUtils.isNotEmpty(id)) {
            builder.append("/").append(id);
        }

        if (StringUtils.isNotEmpty(queryString)) {
            builder.append("?").append(queryString);
        }
        return builder.toString();
    }
}
