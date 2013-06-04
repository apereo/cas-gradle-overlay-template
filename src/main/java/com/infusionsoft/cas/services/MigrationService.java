package com.infusionsoft.cas.services;

import java.text.ParseException;
import java.util.Date;

public interface MigrationService {
    Date getMigrationDate() throws ParseException;

    Integer getDaysToMigrate() throws ParseException;
}
