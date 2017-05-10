package org.apereo.cas.infusionsoft.support;

import org.apereo.cas.infusionsoft.services.AuditService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.springframework.scheduling.annotation.Scheduled;

public class GarbageMan {

    private UserService userService;

    private AuditService auditService;

    public GarbageMan(UserService userService, AuditService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    @Scheduled(initialDelay = 300000, fixedRate = 300000)
    public void cleanup() {
        userService.cleanupLoginAttempts();
        auditService.cleanupOldAuditEntries();
    }

}
