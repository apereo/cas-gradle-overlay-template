package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.MigratedApp;

import java.text.ParseException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Brad
 * Date: 4/11/13
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MigrationService {
    boolean isAppMigrated(String appName, String appType);

    Date getMigrationDate() throws ParseException;

    Integer getDaysToMigrate() throws ParseException;

    void save(MigratedApp migratedApp);
}
