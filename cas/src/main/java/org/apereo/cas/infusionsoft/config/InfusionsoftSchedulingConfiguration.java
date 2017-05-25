package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.infusionsoft.services.AuditService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.infusionsoft.support.GarbageMan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration("infusionsoftSchedulingConfiguration")
@EnableScheduling
public class InfusionsoftSchedulingConfiguration {

    @Autowired
    AuditService auditService;

    @Autowired
    private UserService userService;

    @Bean
    public GarbageMan garbageMan() {
        return new GarbageMan(userService, auditService);
    }
}
