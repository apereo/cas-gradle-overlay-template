package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OAuthServiceConfigDAO extends PagingAndSortingRepository<OAuthServiceConfig, Long> {
    OAuthServiceConfig findByName (String name);
}
