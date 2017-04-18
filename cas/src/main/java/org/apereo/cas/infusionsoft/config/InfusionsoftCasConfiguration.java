package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftAuthenticationHandler;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftSocialLoginPrincipalFactory;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.*;
import org.apereo.cas.infusionsoft.services.*;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apereo.cas.infusionsoft.web.controllers.PasswordCheckController;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration("infusionsoftCasConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class, InfusionsoftConfigurationProperties.class})
public class InfusionsoftCasConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    private CasConfigurationProperties casConfigurationProperties;

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Autowired
    private LoginAttemptDAO loginAttemptDAO;

    @Autowired
    private MarketingOptionsDAO marketingOptionsDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PrincipalFactory principalFactory;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserAccountDAO userAccountDAO;

    @Autowired
    private UserPasswordDAO userPasswordDAO;

    @Bean
    public AppHelper appHelper() {
        return new AppHelper(crmService(), customerHubService());
    }

    @Bean
    public PrincipalFactory clientPrincipalFactory() {
        return new InfusionsoftSocialLoginPrincipalFactory(userService);
    }

    @Bean
    public CrmService crmService() {
        return new CrmService(infusionsoftConfigurationProperties.getCrm());
    }

    @Bean
    public CustomerHubService customerHubService() {
        return new CustomerHubService(infusionsoftConfigurationProperties.getCustomerhub());
    }

    @Bean
    InfusionsoftAuthenticationHandler infusionsoftAuthenticationHandler() {
        return new InfusionsoftAuthenticationHandler("Infusionsoft Authentication Handler", servicesManager, principalFactory, 0, infusionsoftAuthenticationService(), appHelper(), userService());
    }

    @Bean
    public InfusionsoftAuthenticationService infusionsoftAuthenticationService() {
        return new InfusionsoftAuthenticationServiceImpl(casConfigurationProperties, infusionsoftConfigurationProperties, loginAttemptDAO, userService(), passwordService());
    }

    @Bean
    public MarketingOptionsService marketingOptionsService() {
        return new MarketingOptionsServiceImpl(marketingOptionsDAO);
    }

    @Bean
    public PasswordCheckController passwordCheckController() {
        return new PasswordCheckController(infusionsoftAuthenticationService(), passwordService());
    }

    @Bean
    public PasswordService passwordService() {
        return new PasswordServiceImpl(passwordEncoder, userPasswordDAO);
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userDAO, userAccountDAO);
    }

    @Override
    public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(infusionsoftAuthenticationHandler());
    }

}
