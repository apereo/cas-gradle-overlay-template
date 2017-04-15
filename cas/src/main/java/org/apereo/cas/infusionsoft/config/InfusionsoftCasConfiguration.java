package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftAuthenticationHandler;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration("infusionsoftCasConfiguration")
@ComponentScan("org.apereo.cas.infusionsoft.*")
public class InfusionsoftCasConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    private AppHelper appHelper;

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private PrincipalFactory principalFactory;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    private UserService userService;

    @Bean
    InfusionsoftAuthenticationHandler infusionsoftAuthenticationHandler() {
        return new InfusionsoftAuthenticationHandler("Infusionsoft Authentication Handler", servicesManager, principalFactory, 0, infusionsoftAuthenticationService, appHelper, userService);
    }

    @Override
    public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(infusionsoftAuthenticationHandler());
    }

}
