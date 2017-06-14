package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.SecurityQuestionDAO;
import org.apereo.cas.infusionsoft.dao.SecurityQuestionResponseDAO;
import org.apereo.cas.infusionsoft.services.*;
import org.apereo.cas.infusionsoft.support.UserAccountTransformer;
import org.apereo.cas.infusionsoft.web.controllers.AuthenticateController;
import org.apereo.cas.infusionsoft.web.controllers.PasswordCheckController;
import org.apereo.cas.infusionsoft.web.controllers.RegistrationController;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("infusionsoftMvcConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class, InfusionsoftConfigurationProperties.class})
public class InfusionsoftMvcConfiguration {

    @Autowired
    private UserAccountTransformer userAccountTransformer;

    @Autowired
    private AuditService auditService;

    @Autowired(required = false)
    @Qualifier("defaultAuthenticationSystemSupport")
    private AuthenticationSystemSupport authenticationSystemSupport;

    @Autowired
    private CasConfigurationProperties casConfigurationProperties;

    @Autowired
    @Qualifier("centralAuthenticationService")
    private CentralAuthenticationService centralAuthenticationService;

    @Autowired
    @Qualifier("ticketGrantingTicketCookieGenerator")
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Autowired
    private MailService mailService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private SecurityQuestionDAO securityQuestionDAO;

    @Autowired
    private SecurityQuestionResponseDAO securityQuestionResponseDAO;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    private TicketRegistry ticketRegistry;

    @Autowired
    UserService userService;

    @Bean
    public AuthenticateController authenticateController() {
        return new AuthenticateController(infusionsoftAuthenticationService, userAccountTransformer, userService, messageSource, auditService);
    }

    @Bean
    public AutoLoginService autoLoginService() {
        return new AutoLoginService(centralAuthenticationService, ticketGrantingTicketCookieGenerator, ticketRegistry, authenticationSystemSupport);
    }

    @Bean
    public PasswordCheckController passwordCheckController() {
        return new PasswordCheckController(passwordService, userService, autoLoginService());
    }

    @Bean
    public RegistrationController registrationController() {
        return new RegistrationController(autoLoginService(),
                infusionsoftAuthenticationService, casConfigurationProperties, infusionsoftConfigurationProperties,
                mailService, passwordService, securityQuestionService(), servicesManager, userService,
                casConfigurationProperties.getView().getDefaultRedirectUrl()
        );
    }

    @Bean
    public SecurityQuestionService securityQuestionService() {
        return new SecurityQuestionServiceImpl(securityQuestionDAO, securityQuestionResponseDAO, infusionsoftConfigurationProperties);
    }
}
