package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.MigratedApp;

import java.util.List;

public interface MigratedAppDAO extends JpaDAO<MigratedApp> {
    List<MigratedApp> findByAppNameAndAppType(String appName, String appType);
}
