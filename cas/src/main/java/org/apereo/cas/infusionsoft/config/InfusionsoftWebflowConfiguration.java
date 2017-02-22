package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.services.BuildServiceImpl;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.MarketingOptionsService;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apereo.cas.infusionsoft.webflow.InfusionsoftFlowSetupAction;
import org.apereo.cas.infusionsoft.webflow.InfusionsoftWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

@Configuration("infusionsoftWebflowConfiguration")
@EnableConfigurationProperties({InfusionsoftConfigurationProperties.class})
public class InfusionsoftWebflowConfiguration {

    @Autowired
    AppHelper appHelper;

    @Autowired
    BuildServiceImpl buildService;

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Autowired
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Autowired
    @Qualifier("logoutFlowRegistry")
    private FlowDefinitionRegistry logoutFlowDefinitionRegistry;

    @Autowired
    private FlowBuilderServices flowBuilderServices;

    @Autowired
    private MarketingOptionsService marketingOptionsService;

    @ConditionalOnMissingBean(name = "oauth20LogoutWebflowConfigurer")
    @Bean
    public CasWebflowConfigurer infusionsoftWebflowConfigurer() {
        final InfusionsoftWebflowConfigurer c = new InfusionsoftWebflowConfigurer(infusionsoftFlowSetupAction());
        c.setFlowBuilderServices(this.flowBuilderServices);
        c.setLoginFlowDefinitionRegistry(this.loginFlowDefinitionRegistry);
        c.setLogoutFlowDefinitionRegistry(this.logoutFlowDefinitionRegistry);
        return c;
    }

    @Bean
    public InfusionsoftFlowSetupAction infusionsoftFlowSetupAction() {
        return new InfusionsoftFlowSetupAction(
                appHelper,
                buildService,
                infusionsoftAuthenticationService,
                marketingOptionsService,
                infusionsoftConfigurationProperties.getSupportPhoneNumbers());
    }
}
