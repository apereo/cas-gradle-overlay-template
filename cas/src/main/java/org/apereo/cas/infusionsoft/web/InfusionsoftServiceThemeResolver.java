package org.apereo.cas.infusionsoft.web;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.web.ServiceThemeResolver;
import org.apereo.cas.web.support.ArgumentExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Overrides the default theme resolver to look in the request scope as well as the flow scope for the service parameter
 */
public class InfusionsoftServiceThemeResolver extends ServiceThemeResolver {

    private static final Logger log = LoggerFactory.getLogger(ServiceThemeResolver.class);

    private ServicesManager servicesManager;
    private final List<ArgumentExtractor> argumentExtractors;

    public InfusionsoftServiceThemeResolver(String defaultThemeName, ServicesManager servicesManager, Map<String, String> mobileOverrides, List<ArgumentExtractor> argumentExtractors) {
        super(defaultThemeName, servicesManager, mobileOverrides);
        this.servicesManager = servicesManager;
        this.argumentExtractors = argumentExtractors;
    }

    @Override
    public String resolveThemeName(HttpServletRequest request) {
        String themeName = super.resolveThemeName(request);
        if (StringUtils.isNotBlank(themeName) && !StringUtils.equals(themeName, getDefaultThemeName())) {
            return themeName;
        }

        // The key difference between this class and the superclass is this looks at more parameter names
        // and looks for them in the request scope instead of flow scope
        String serviceUrl = request.getParameter("service");
        if (StringUtils.isBlank(serviceUrl)) {
            serviceUrl = request.getParameter("returnUrl");
        }
        if (StringUtils.isNotBlank(serviceUrl)) {
            final RegisteredService rService = this.servicesManager.findServiceBy(serviceUrl);
            if (rService != null && rService.getAccessStrategy().isServiceAccessAllowed()
                    && StringUtils.isNotBlank(rService.getTheme())) {
                log.debug("Service [{}] is configured to use a custom theme [{}]", rService, rService.getTheme());
                final CasThemeResourceBundleMessageSource messageSource = new CasThemeResourceBundleMessageSource();
                messageSource.setBasename(rService.getTheme());
                if (messageSource.doGetBundle(rService.getTheme(), request.getLocale()) != null) {
                    log.debug("Found custom theme [{}] for service [{}]", rService.getTheme(), rService);
                    return rService.getTheme();
                } else {
                    log.warn("Custom theme [{}] for service [{}] cannot be located. Falling back to default theme...",
                            rService.getTheme(), rService);
                }
            }
        }
        return getDefaultThemeName();
    }

    private static class CasThemeResourceBundleMessageSource extends ResourceBundleMessageSource {
        @Override
        protected ResourceBundle doGetBundle(final String basename, final Locale locale) {
            try {
                final ResourceBundle bundle = ResourceBundle.getBundle(basename, locale, getBundleClassLoader());
                if (bundle != null && !bundle.keySet().isEmpty()) {
                    return bundle;
                }
            } catch (final Exception e) {
                log.debug(e.getMessage(), e);
            }
            return null;
        }
    }
}
