package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.web.InfusionsoftServiceThemeResolver;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.support.ArgumentExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ThemeResolver;

import java.util.Collections;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class InfusionsoftThemesConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    private ServicesManager servicesManager;

    @Autowired
    @Qualifier("serviceThemeResolverSupportedBrowsers")
    private Map serviceThemeResolverSupportedBrowsers;

    @Autowired
    private ArgumentExtractor argumentExtractor;

    @Bean
    public ThemeResolver themeResolver() {
        final String defaultThemeName = casProperties.getTheme().getDefaultThemeName();
        //noinspection unchecked
        return new InfusionsoftServiceThemeResolver(defaultThemeName, servicesManager, serviceThemeResolverSupportedBrowsers, Collections.singletonList(argumentExtractor));
    }

}
