package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.MigratedAppDAO;
import com.infusionsoft.cas.domain.MigratedApp;
import org.apache.log4j.Logger;
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
import java.util.List;

@Transactional
@Service("migrationService")
public class MigrationServiceImpl implements MigrationService {

    private static final Logger log = Logger.getLogger(MigrationServiceImpl.class);

    @Autowired
    MigratedAppDAO migratedAppDAO;

    @Autowired
    InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Value("${infusionsoft.migration.date}")
    String migrationDate;

    /**
     * Checks whether an app has been fully migrated to CAS.
     */
    @Override
    public boolean isAppMigrated(String appName, String appType) {
        List<MigratedApp> results = migratedAppDAO.findByAppNameAndAppType(appName, appType);
        boolean migrated = results.size() > 0;

        log.debug("has app " + appName + "/" + appType + " been migrated to CAS? " + migrated);

        return migrated;
    }

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

    @Override
    public void save(MigratedApp migratedApp) {
        migratedAppDAO.save(migratedApp);
    }

//    private void interpretServiceUrl(HttpServletRequest request) {
//        String service = request.getParameter("service");
//
//        if (StringUtils.isNotEmpty(service)) {
//            request.getSession(true).setAttribute("serviceUrl", service);
//
//            log.debug("service url " + service + " has been saved in the session");
//
//            try {
//                URL serviceUrl = new URL(service);
//                String appName = infusionsoftAuthenticationService.guessAppName(serviceUrl);
//                String appType = infusionsoftAuthenticationService.guessAppType(serviceUrl);
//
//                if (StringUtils.equals(appType, AppType.CRM)) {
//                    String refererUrl = appHelper.buildAppUrl(appType, appName);
//
//                    request.getSession().setAttribute("refererUrl", refererUrl);
//                    request.getSession().setAttribute("refererAppName", appName);
//                    request.getSession().setAttribute("refererAppType", appType);
//
//                    log.debug("stored referer app info in session: " + appName + "/" + appType);
//                }
//
//                if (StringUtils.isNotEmpty(appName) && StringUtils.isNotEmpty(appType)) {
//                    request.setAttribute("appMigrated", migrationService.isAppMigrated(appName, appType));
//                }
//            } catch (Exception e) {
//                log.warn("couldn't parse and interpret service url: " + service, e);
//            }
//        }
//    }

}
