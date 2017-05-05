package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.Authority;
import org.springframework.data.repository.PagingAndSortingRepository;

@Deprecated
public interface AuthorityDAO extends PagingAndSortingRepository<Authority, Long> {

    @Deprecated
    Authority findByAuthority(String authority);

}
