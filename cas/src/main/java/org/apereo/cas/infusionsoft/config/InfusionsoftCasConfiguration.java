package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.principal.resolvers.EchoingPrincipalResolver;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftAuthenticationHandler;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftSocialLoginPrincipalFactory;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.*;
import org.apereo.cas.infusionsoft.services.*;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apereo.cas.infusionsoft.support.GarbageMan;
import org.apereo.cas.infusionsoft.web.controllers.AuthenticateController;
import org.apereo.cas.infusionsoft.web.controllers.PasswordCheckController;
import org.apereo.cas.infusionsoft.web.controllers.RegistrationController;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration("infusionsoftCasConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class, InfusionsoftConfigurationProperties.class})
public class InfusionsoftCasConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    private AuthorityDAO authorityDAO;

    @Autowired
    private CasConfigurationProperties casConfigurationProperties;

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Autowired
    private LoginAttemptDAO loginAttemptDAO;

    @Autowired
    private MailService mailService;

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
    @Qualifier("ticketGrantingTicketCookieGenerator")
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    @Autowired
    private TicketRegistry ticketRegistry;

    @Autowired
    private UserAccountDAO userAccountDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserIdentityDAO userIdentityDAO;

    @Autowired
    private UserPasswordDAO userPasswordDAO;

    @Bean
    public AppHelper appHelper() {
        return new AppHelper(infusionsoftConfigurationProperties);
    }

    @Bean
    public PrincipalFactory clientPrincipalFactory() {
        return new InfusionsoftSocialLoginPrincipalFactory(userService());
    }

    @Bean
    InfusionsoftAuthenticationHandler infusionsoftAuthenticationHandler() {
        return new InfusionsoftAuthenticationHandler("Infusionsoft Authentication Handler", servicesManager, principalFactory, 0, infusionsoftAuthenticationService(), userService());
    }

    @Bean
    public InfusionsoftAuthenticationService infusionsoftAuthenticationService() {
        return new InfusionsoftAuthenticationServiceImpl(ticketRegistry, loginAttemptDAO, userService(), passwordService(), ticketGrantingTicketCookieGenerator, casConfigurationProperties, infusionsoftConfigurationProperties);
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

    @RefreshScope
    @Bean
    public PrincipalResolver personDirectoryPrincipalResolver() {
        return new EchoingPrincipalResolver();
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(appHelper(), authorityDAO, loginAttemptDAO, mailService, passwordService(), userDAO, userAccountDAO, userIdentityDAO, infusionsoftConfigurationProperties);
    }

    @Override
    public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(infusionsoftAuthenticationHandler());
    }

}
