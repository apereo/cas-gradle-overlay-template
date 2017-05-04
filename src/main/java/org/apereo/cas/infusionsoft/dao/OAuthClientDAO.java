package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.OAuthClient;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OAuthClientDAO extends PagingAndSortingRepository<OAuthClient, Long> {
    OAuthClient findByClientId(String clientId);
}
