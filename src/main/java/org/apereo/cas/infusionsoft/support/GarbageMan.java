package org.apereo.cas.infusionsoft.support;

import org.apereo.cas.infusionsoft.services.AuditService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GarbageMan {

    @Autowired
    UserService userService;

    @Autowired
    AuditService auditService;

    public void cleanup() {
        userService.cleanupLoginAttempts();
        auditService.cleanupOldAuditEntries();
    }
}
