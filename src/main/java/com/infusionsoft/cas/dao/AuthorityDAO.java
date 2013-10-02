package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.Authority;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuthorityDAO extends PagingAndSortingRepository<Authority, Long> {

    Authority findByAuthority(String authority);

}
