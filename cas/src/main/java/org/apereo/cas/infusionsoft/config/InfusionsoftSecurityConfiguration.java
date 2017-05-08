package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.configuration.support.Beans;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties(InfusionsoftConfigurationProperties.class)
public class InfusionsoftSecurityConfiguration {

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftProperties;

    @Bean
    PasswordEncoder passwordEncoder() {
        return Beans.newPasswordEncoder(infusionsoftProperties.getPasswordEncoder());
    }

}
