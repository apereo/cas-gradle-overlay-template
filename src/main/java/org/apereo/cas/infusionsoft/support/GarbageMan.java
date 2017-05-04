package org.apereo.cas.infusionsoft.support;

import org.apereo.cas.infusionsoft.services.AuditService;
import org.apereo.cas.infusionsoft.services.ServiceTicketService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GarbageMan {
    @Autowired
    UserService userService;
    @Autowired
    ServiceTicketService serviceTicketService;
    @Autowired
    AuditService auditService;

    public void cleanup() {
        userService.cleanupLoginAttempts();
        serviceTicketService.deleteOrphanedServiceTicketRecords();
        auditService.cleanupOldAuditEntries();
    }
}
