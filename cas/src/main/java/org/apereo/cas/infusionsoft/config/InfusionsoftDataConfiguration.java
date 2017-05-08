package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jpa.JpaConfigDataHolder;
import org.apereo.cas.configuration.support.Beans;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties({CasConfigurationProperties.class, InfusionsoftConfigurationProperties.class})
@EnableJpaRepositories("org.apereo.cas.infusionsoft.dao")
@EnableTransactionManagement(proxyTargetClass = true)
public class InfusionsoftDataConfiguration {

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Autowired
    private CasConfigurationProperties casProperties;

    @RefreshScope
    @Bean
    public HibernateJpaVendorAdapter jpaInfusionsoftVendorAdapter() {
        return Beans.newHibernateJpaVendorAdapter(casProperties.getJdbc());
    }

    @RefreshScope
    @Bean
    public DataSource dataSource() {
        return Beans.newHickariDataSource(infusionsoftConfigurationProperties.getJpa());
    }

    public String[] jpaInfusionsoftPackagesToScan() {
        return new String[]{"org.apereo.cas.infusionsoft.domain"};
    }

    @Lazy
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

/*
    TODO: upgrade

    <beans:bean id="validator" class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean">
        <beans:property name="validationMessageSource" ref="messageSource"/>
    </beans:bean>

    <beans:bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <beans:property name="jpaVendorAdapter">
            <beans:bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
                <beans:property name="generateDdl" value="true"/>
                <!--<beans:property name="showSql" value="true"/>-->
                <beans:property name="databasePlatform" value="org.hibernate.dialect.MySQL5Dialect"/>
            </beans:bean>
        </beans:property>
        <beans:property name="jpaPropertyMap">
            <beans:map>
                <beans:entry key="javax.persistence.validation.factory" value-ref="validator" />
            </beans:map>
        </beans:property>
    </beans:bean>
*/

        final LocalContainerEntityManagerFactoryBean bean =
                Beans.newHibernateEntityManagerFactoryBean(
                        new JpaConfigDataHolder(
                                jpaInfusionsoftVendorAdapter(),
                                "jpaInfusionsoftContext",
                                jpaInfusionsoftPackagesToScan(),
                                dataSource()),
                        infusionsoftConfigurationProperties.getJpa());

        bean.getJpaPropertyMap().put("hibernate.enable_lazy_load_no_trans", Boolean.TRUE);
        return bean;
    }

    @Autowired
    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") final EntityManagerFactory emf) {
        final JpaTransactionManager mgmr = new JpaTransactionManager();
        mgmr.setEntityManagerFactory(emf);
        return mgmr;
    }

/*
    TODO: upgrade

    <!--
      Injects EntityManager/Factory instances into beans with
      @PersistenceUnit and @PersistenceContext
    -->
    <beans:bean class="org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor"/>

    <beans:bean id="flyway" class="com.googlecode.flyway.core.Flyway" init-method="migrate" depends-on="dataSource">
        <beans:property name="dataSource" ref="dataSource"/>
        <beans:property name="initOnMigrate" value="true"/>
    </beans:bean>

 */
}
