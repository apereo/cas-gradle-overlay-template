package org.apereo.cas.infusionsoft.config;

import org.apache.velocity.app.VelocityEngine;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(InfusionsoftConfigurationProperties.class)
public class InfusionsoftMailConfiguration {

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftProperties;

    @Autowired
    private MessageSource messageSource;

    @Bean
    JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(infusionsoftProperties.getMail().getDomain());

        return mailSender;
    }

    @Bean
    public MailService mailService() throws IOException {
        return new MailService(mailSender(), velocityEngine(), messageSource, infusionsoftProperties);
    }

    @Bean()
    public VelocityEngineFactoryBean velocityEngineFactory() throws IOException {
        final VelocityEngineFactoryBean velocityEngineFactory = new VelocityEngineFactoryBean();
        Map<String, Object> velocityProperties = new HashMap<>();
        velocityProperties.put("resource.loader", "classpath");
        velocityProperties.put("classpath.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngineFactory.setVelocityPropertiesMap(velocityProperties);
        return velocityEngineFactory;
    }

    @Bean
    public VelocityEngine velocityEngine() throws IOException {
        return velocityEngineFactory().getObject();
    }

}
