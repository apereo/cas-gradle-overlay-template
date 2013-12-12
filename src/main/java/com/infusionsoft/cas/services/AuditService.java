package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.AuditEntry;
import com.infusionsoft.cas.domain.User;

public interface AuditService {
    public void logApiLoginSuccess(User user);

    public void logApiLoginFailure(String username);

    public void saveAuditEntry(AuditEntry entry);

    public void cleanupOldAuditEntries();
}
