package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.AuditEntry;
import org.apereo.cas.infusionsoft.domain.User;

public interface AuditService {

    void logApiLoginSuccess(User user);

    void logApiLoginFailure(String username);

    void saveAuditEntry(AuditEntry entry);

    void cleanupOldAuditEntries();

}
