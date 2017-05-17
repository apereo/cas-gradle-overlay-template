package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration("infusionsoftCasConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class, InfusionsoftConfigurationProperties.class})
@EnableScheduling
public class InfusionsoftCasConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    @Lazy
    private AuditService auditService;

    @Autowired
    @Lazy
    private AuthenticationSystemSupport authenticationSystemSupport;

    @Autowired
    private AuthorityDAO authorityDAO;

    @Autowired
    private CasConfigurationProperties casConfigurationProperties;

    @Autowired
    private CentralAuthenticationService centralAuthenticationService;

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Autowired
    private LoginAttemptDAO loginAttemptDAO;

    @Autowired
    private MailService mailService;

    @Autowired
    private MarketingOptionsDAO marketingOptionsDAO;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PendingUserAccountDAO pendingUserAccountDAO;

    @Autowired
    private PrincipalFactory principalFactory;

    @Autowired
    private SecurityQuestionDAO securityQuestionDAO;

    @Autowired
    private SecurityQuestionResponseDAO securityQuestionResponseDAO;

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
        return new AppHelper(crmService(), customerHubService(), infusionsoftConfigurationProperties);
    }

    @Bean
    public AuthenticateController authenticateController() {
        return new AuthenticateController(infusionsoftAuthenticationService(), appHelper(), userService(), messageSource, auditService);
    }

    @Bean
    public AutoLoginService autoLoginService() {
        return new AutoLoginService(centralAuthenticationService, ticketGrantingTicketCookieGenerator, ticketRegistry, authenticationSystemSupport);
    }

    @Bean
    public PrincipalFactory clientPrincipalFactory() {
        return new InfusionsoftSocialLoginPrincipalFactory(userService());
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
    @Autowired
    public GarbageMan garbageMan(UserService userService, AuditService auditService) {
        return new GarbageMan(userService, auditService);
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

    @Bean
    public RegistrationController registrationController() {
        return new RegistrationController(appHelper(), autoLoginService(), crmService(), customerHubService(),
                infusionsoftAuthenticationService(), infusionsoftConfigurationProperties,
                mailService, passwordService(), securityQuestionService(), servicesManager, userService()
        );
    }

    @Bean
    public SecurityQuestionService securityQuestionService() {
        return new SecurityQuestionServiceImpl(securityQuestionDAO, securityQuestionResponseDAO, infusionsoftConfigurationProperties);
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(appHelper(), authorityDAO, loginAttemptDAO, mailService, passwordService(), pendingUserAccountDAO, userDAO, userAccountDAO, userIdentityDAO, infusionsoftConfigurationProperties);
    }

    @Override
    public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(infusionsoftAuthenticationHandler());
    }

    @RefreshScope
    @Bean
    public PrincipalResolver personDirectoryPrincipalResolver() {
        return new EchoingPrincipalResolver();
    }

}
