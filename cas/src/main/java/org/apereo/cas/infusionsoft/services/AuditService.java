package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.User;

public interface AuditService {

    void logApiLoginSuccess(User user);

    void logApiLoginFailure(String username);

    void cleanupOldAuditEntries();

}
