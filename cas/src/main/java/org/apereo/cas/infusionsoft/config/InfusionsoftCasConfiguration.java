package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.infusionsoft.authentication.InfusionsoftAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration("infusionsoftCasConfiguration")
public class InfusionsoftCasConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    private InfusionsoftAuthenticationHandler infusionsoftAuthenticationHandler;

    @Autowired
    @Qualifier("personDirectoryPrincipalResolver")
    private PrincipalResolver personDirectoryPrincipalResolver;

    @Autowired
    @Qualifier("authenticationHandlersResolvers")
    private Map authenticationHandlersResolvers;

    @PostConstruct
    public void initializeInfusionsoftAuthenticationHandlers() {
        authenticationHandlersResolvers.put(infusionsoftAuthenticationHandler, personDirectoryPrincipalResolver);
    }




//    @Autowired
//    InfusionsoftAuthenticationHandler infusionsoftAuthenticationHandler;

//    @Bean
//    public Collection<AuthenticationHandler> infusionsoftAuthenticationHandlers() {
//        final Collection<AuthenticationHandler> handlers = new HashSet<>();
//
//        handlers.add(infusionsoftAuthenticationHandler);
//
//        return handlers;
//    }
}
