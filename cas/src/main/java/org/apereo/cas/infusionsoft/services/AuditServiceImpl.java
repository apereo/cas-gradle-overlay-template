package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.AuditEntryDAO;
import org.apereo.cas.infusionsoft.domain.AuditEntry;
import org.apereo.cas.infusionsoft.domain.AuditEntryType;
import org.apereo.cas.infusionsoft.domain.User;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for accessing our audit logs.
 */
@Transactional(transactionManager = "transactionManager")
public class AuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    private AuditEntryDAO auditEntryDAO;
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    public AuditServiceImpl(AuditEntryDAO auditEntryDAO, InfusionsoftConfigurationProperties infusionsoftConfigurationProperties) {
        this.auditEntryDAO = auditEntryDAO;
        this.infusionsoftConfigurationProperties = infusionsoftConfigurationProperties;
    }

    public void logApiLoginSuccess(User user) {
        AuditEntry entry = new AuditEntry();

        entry.setType(AuditEntryType.ApiLoginSuccess);
        entry.setUsername(user.getUsername());
        entry.setUserId(user.getId());
        entry.setDate(DateTime.now(DateTimeZone.UTC));

        saveAuditEntry(entry);
    }

    public void logApiLoginFailure(String username) {
        AuditEntry entry = new AuditEntry();

        entry.setType(AuditEntryType.ApiLoginFail);
        entry.setUsername(username);
        entry.setDate(DateTime.now(DateTimeZone.UTC));

        saveAuditEntry(entry);
    }

    public void saveAuditEntry(AuditEntry entry) {
        auditEntryDAO.save(entry);
    }

    public void cleanupOldAuditEntries() {
        final long auditEntryMaxAge = infusionsoftConfigurationProperties.getAuditEntryMaxAge();
        log.info("cleaning up audit entries older than " + auditEntryMaxAge + " ms");

        DateTime date = new DateTime(System.currentTimeMillis() - auditEntryMaxAge);
        List<AuditEntry> entries = auditEntryDAO.findByDateLessThan(date);

        log.info("deleting " + entries.size() + " audit entries that occurred before " + date);

        auditEntryDAO.delete(entries);
    }

}
