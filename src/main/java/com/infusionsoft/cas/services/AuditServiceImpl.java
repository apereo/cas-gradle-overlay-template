package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.AuditEntryDAO;
import com.infusionsoft.cas.domain.AuditEntry;
import com.infusionsoft.cas.domain.AuditEntryType;
import com.infusionsoft.cas.domain.User;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for accessing our audit logs.
 */
@Service
@Transactional
public class AuditServiceImpl implements AuditService {
    private static final Logger log = Logger.getLogger(AuditServiceImpl.class);

    @Autowired
    private AuditEntryDAO auditEntryDAO;

    @Value("${infusionsoft.cas.garbageman.auditentrymaxage}")
    private long auditEntryMaxAge = 86400000 * 7; // default to 7 days

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
        log.info("cleaning up audit entries older than " + auditEntryMaxAge + " ms");

        DateTime date = new DateTime(System.currentTimeMillis() - auditEntryMaxAge);
        List<AuditEntry> entries = auditEntryDAO.findByDateLessThan(date);

        log.info("deleting " + entries.size() + " audit entries that occurred before " + date);

        auditEntryDAO.delete(entries);
    }
}
