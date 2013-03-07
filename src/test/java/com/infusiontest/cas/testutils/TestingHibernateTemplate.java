package com.infusiontest.cas.testutils;

import com.infusionsoft.cas.types.*;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Hibernate template that we can use for unit tests. All the configuration is in here to create an in-memory database.
 */
public class TestingHibernateTemplate extends HibernateTemplate {
    public TestingHibernateTemplate() {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(CommunityAccountDetails.class);
        configuration.addAnnotatedClass(LegacyAccount.class);
        configuration.addAnnotatedClass(LoginAttempt.class);
        configuration.addAnnotatedClass(MigratedApp.class);
        configuration.addAnnotatedClass(PendingUserAccount.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(UserAccount.class);
        configuration.addAnnotatedClass(UserPassword.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");

        setSessionFactory(configuration.buildSessionFactory());
        setAllowCreate(true);
    }
}
