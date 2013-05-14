package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.Authority;
import com.infusionsoft.cas.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Set;

public interface AuthorityDAO extends PagingAndSortingRepository<Authority, Long> {

    Authority findByAuthority(String authority);

}
