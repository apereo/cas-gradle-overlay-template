package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.MigratedApp;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MigratedAppDAO extends PagingAndSortingRepository<MigratedApp, Long> {
    List<MigratedApp> findByAppNameAndAppType(String appName, String appType);
}
