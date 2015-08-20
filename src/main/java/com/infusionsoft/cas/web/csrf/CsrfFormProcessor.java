package com.infusionsoft.cas.web.csrf;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Special form processor that adds a hidden field to every Spring-generated web form, containing a CSRF token.
 */
public class CsrfFormProcessor implements RequestDataValueProcessor {
    private static final Logger log = Logger.getLogger(CsrfFormProcessor.class);

    @Value("${csrf.token.name}")
    private String tokenName;

    @Autowired
    private CsrfTokenManager csrfTokenManager;

    @Override
    public String processAction(HttpServletRequest request, String action) {
        return action;
    }

    @Override
    public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) {
        return value;
    }

    @Override
    public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
        Map<String, String> hiddenFields = new HashMap<String, String>();

        if (StringUtils.isNotEmpty(tokenName)) {
            hiddenFields.put(tokenName, csrfTokenManager.getExpectedCsrfTokenForRequest(request));
        } else {
            log.warn("CSRF is not properly configured");
        }

        return hiddenFields;
    }

    @Override
    public String processUrl(HttpServletRequest request, String url) {
        return url;
    }
}
