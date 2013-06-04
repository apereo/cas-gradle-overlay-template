package com.infusionsoft.cas.services;

import org.joda.time.DateMidnight;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Transactional
@Service("migrationService")
public class MigrationServiceImpl implements MigrationService {

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Value("${infusionsoft.migration.date}")
    String migrationDate;

    @Override
    public Date getMigrationDate() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        DateMidnight migration = new DateMidnight(format.parse(migrationDate).getTime());

        return migration.toDate();
    }

    @Override
    public Integer getDaysToMigrate() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        DateMidnight migration = new DateMidnight(format.parse(migrationDate).getTime());
        DateMidnight today = new DateMidnight(System.currentTimeMillis());

        Days daysToMigrate = Days.daysBetween(today, migration);

        return daysToMigrate.getDays();
    }
}
