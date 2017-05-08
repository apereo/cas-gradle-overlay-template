package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.AuditEntryDAO;
import org.apereo.cas.infusionsoft.services.AuditService;
import org.apereo.cas.infusionsoft.services.AuditServiceImpl;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.infusionsoft.support.InfusionsoftAuditTrailManager;
import org.apereo.cas.services.ServicesManager;
import org.apereo.inspektr.audit.AuditTrailManagementAspect;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.ArrayList;
import java.util.List;

@Configuration("casCoreAuditConfiguration")
@EnableAspectJAutoProxy
@EnableConfigurationProperties({CasConfigurationProperties.class, InfusionsoftConfigurationProperties.class})
public class InfusionsoftAuditConfiguration extends CasCoreAuditConfiguration {

    @Autowired
    private AuditEntryDAO auditEntryDAO;

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    private UserService userService;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Bean
    public AuditTrailManagementAspect auditTrailManagementAspect(@Qualifier("auditTrailManager") final AuditTrailManager auditTrailManager) {
        List<AuditTrailManager> auditTrailManagers = new ArrayList<>();
        auditTrailManagers.add(auditTrailManager);
        auditTrailManagers.add(infusionsoftAuditTrailManager());

        final AuditTrailManagementAspect aspect = new AuditTrailManagementAspect(
                casProperties.getAudit().getAppCode(),
                auditablePrincipalResolver(principalIdProvider()),
                auditTrailManagers, auditActionResolverMap(),
                auditResourceResolverMap());
        aspect.setFailOnAuditFailures(!casProperties.getAudit().isIgnoreAuditFailures());
        return aspect;
    }

    @Bean
    public AuditService auditService() {
        return new AuditServiceImpl(auditEntryDAO, infusionsoftConfigurationProperties);
    }

    @Bean
    public InfusionsoftAuditTrailManager infusionsoftAuditTrailManager() {
        return new InfusionsoftAuditTrailManager(auditService(), userService, servicesManager);
    }

}
