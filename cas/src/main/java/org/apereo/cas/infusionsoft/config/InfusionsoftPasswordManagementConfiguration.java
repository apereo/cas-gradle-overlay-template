package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.CipherExecutor;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.services.InfusionsoftPasswordManagementService;
import org.apereo.cas.infusionsoft.services.PasswordService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.infusionsoft.webflow.InfusionsoftInsertCredentialAction;
import org.apereo.cas.infusionsoft.webflow.InfusionsoftPasswordManagementWebflowConfigurer;
import org.apereo.cas.pm.PasswordManagementService;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

import java.io.Serializable;

@Configuration
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class InfusionsoftPasswordManagementConfiguration {

    @Autowired
    @Qualifier("passwordManagementCipherExecutor")
    private CipherExecutor<Serializable, String> cipherExecutor;

    @Autowired
    private FlowBuilderServices flowBuilderServices;

    @Autowired
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private CasConfigurationProperties casConfigurationProperties;

    @Autowired
    private TicketRegistry ticketRegistry;

    @Autowired
    private UserService userService;

    @Bean
    public InfusionsoftPasswordManagementWebflowConfigurer passwordManagementWebflowConfigurer() {
        return new InfusionsoftPasswordManagementWebflowConfigurer(flowBuilderServices, loginFlowDefinitionRegistry, infusionsoftInsertCredentialAction());
    }

    @Bean
    InfusionsoftInsertCredentialAction infusionsoftInsertCredentialAction() {
        return new InfusionsoftInsertCredentialAction(ticketRegistry);
    }

    @Bean
    PasswordManagementService passwordChangeService() {
        return new InfusionsoftPasswordManagementService(
                cipherExecutor,
                "Infusionsoft",
                casConfigurationProperties.getAuthn().getPm(),
                passwordService,
                userService);
    }
}
