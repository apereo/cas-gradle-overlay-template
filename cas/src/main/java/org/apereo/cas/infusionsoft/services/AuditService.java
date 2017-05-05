package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.AuditEntry;
import org.apereo.cas.infusionsoft.domain.User;

public interface AuditService {
    public void logApiLoginSuccess(User user);

    public void logApiLoginFailure(String username);

    public void saveAuditEntry(AuditEntry entry);

    public void cleanupOldAuditEntries();
}
