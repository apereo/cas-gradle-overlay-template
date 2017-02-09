package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@EnableConfigurationProperties(InfusionsoftConfigurationProperties.class)
@ImportAutoConfiguration(VelocityAutoConfiguration.class)
public class InfusionsoftMailConfiguration {

    @Autowired
    InfusionsoftConfigurationProperties infusionsoftProperties;

    @Bean
    JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(infusionsoftProperties.getMail().getDomain());

        return mailSender;
    }

}
