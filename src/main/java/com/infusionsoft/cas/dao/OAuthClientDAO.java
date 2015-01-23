package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.OAuthClient;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OAuthClientDAO extends PagingAndSortingRepository<OAuthClient, Long> {
    OAuthClient findByClientId(String clientId);
}
