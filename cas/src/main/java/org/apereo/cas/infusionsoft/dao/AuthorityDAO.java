package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.Authority;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuthorityDAO extends PagingAndSortingRepository<Authority, Long> {

    Authority findByAuthority(String authority);

}
