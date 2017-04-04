package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration("infusionsoftCasConfiguration")
@ComponentScan("org.apereo.cas.infusionsoft.*")
//@Import({
//        InfusionsoftDataConfiguration.class,
//        InfusionsoftMailConfiguration.class,
//        InfusionsoftRegisteredServicesConfiguration.class,
//        InfusionsoftSecurityConfiguration.class,
//        InfusionsoftWebflowConfiguration.class
//})
public class InfusionsoftCasConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    private InfusionsoftAuthenticationHandler infusionsoftAuthenticationHandler;

    @Override
    public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(infusionsoftAuthenticationHandler);
    }

}
