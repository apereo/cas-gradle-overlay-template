package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.OAuthServiceConfig;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OAuthServiceConfigDAO extends PagingAndSortingRepository<OAuthServiceConfig, Long> {
    OAuthServiceConfig findByName (String name);
}
