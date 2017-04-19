package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.UserIdentity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserIdentityDAO extends PagingAndSortingRepository<UserIdentity, Long> {
    UserIdentity findByExternalId(String externalId);
}
