package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.AuditEntryDAO;
import org.apereo.cas.infusionsoft.services.AuditService;
import org.apereo.cas.infusionsoft.services.AuditServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableConfigurationProperties(InfusionsoftConfigurationProperties.class)
public class InfusionsoftAuditConfiguration {

    @Autowired
    private AuditEntryDAO auditEntryDAO;

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Bean
    public AuditService auditService() {
        return new AuditServiceImpl(auditEntryDAO, infusionsoftConfigurationProperties);
    }

}
