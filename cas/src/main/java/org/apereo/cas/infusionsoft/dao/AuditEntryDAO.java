package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.AuditEntry;
import org.joda.time.DateTime;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AuditEntryDAO extends PagingAndSortingRepository<AuditEntry, Long> {
    public List<AuditEntry> findByDateLessThan(DateTime date);
}
