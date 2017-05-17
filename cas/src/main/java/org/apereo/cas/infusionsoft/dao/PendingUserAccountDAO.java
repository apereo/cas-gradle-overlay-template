package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.PendingUserAccount;
import org.springframework.data.repository.PagingAndSortingRepository;

@Deprecated
public interface PendingUserAccountDAO extends PagingAndSortingRepository<PendingUserAccount, Long> {
    @Deprecated
    PendingUserAccount findByRegistrationCode(String registrationCode);
}
