package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.CipherExecutor;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.pm.PasswordManagementProperties;
import org.apereo.cas.infusionsoft.services.InfusionsoftPasswordManagementService;
import org.apereo.cas.infusionsoft.services.PasswordService;
import org.apereo.cas.infusionsoft.services.SecurityQuestionResponseDAO;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.pm.PasswordManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class InfusionsoftPasswordManagementConfiguration {

    @Autowired
    @Qualifier("passwordManagementCipherExecutor")
    CipherExecutor<Serializable, String> cipherExecutor;

    @Autowired
    PasswordService passwordService;

    @Autowired
    CasConfigurationProperties casConfigurationProperties;

    @Autowired
    SecurityQuestionResponseDAO securityQuestionResponseDAO;

    @Autowired
    UserService userService;

    @Bean
    PasswordManagementService passwordChangeService() {
        return new InfusionsoftPasswordManagementService(
                cipherExecutor,
                "Infusionsoft",
                casConfigurationProperties.getAuthn().getPm(),
                passwordService,
                securityQuestionResponseDAO,
                userService);
    }
}
