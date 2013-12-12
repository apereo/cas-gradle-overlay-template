package com.infusionsoft.cas.support;

import com.infusionsoft.cas.services.AuditService;
import com.infusionsoft.cas.services.ServiceTicketService;
import com.infusionsoft.cas.services.UserService;
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
