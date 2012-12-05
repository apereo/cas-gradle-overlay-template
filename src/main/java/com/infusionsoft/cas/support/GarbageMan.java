package com.infusionsoft.cas.support;


import com.infusionsoft.cas.types.LoginAttempt;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.Date;
import java.util.List;

/**
 * Simple Quartz job that cleans up old login attempts from the database.
 */
public class GarbageMan {
    private static final Logger log = Logger.getLogger(GarbageMan.class);

    private HibernateTemplate hibernateTemplate;
    private long loginAttemptMaxAge = 86400000;

    public void cleanup() {
        Date date = new Date(System.currentTimeMillis() - loginAttemptMaxAge);
        List<LoginAttempt> attempts = (List<LoginAttempt>) hibernateTemplate.find("from LoginAttempt a where a.dateAttempted < ?", date);

        log.info("deleting " + attempts.size() + " login attempts that occurred before " + date);

        hibernateTemplate.deleteAll(attempts);
    }

    public long getLoginAttemptMaxAge() {
        return loginAttemptMaxAge;
    }

    public void setLoginAttemptMaxAge(long loginAttemptMaxAge) {
        this.loginAttemptMaxAge = loginAttemptMaxAge;
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
