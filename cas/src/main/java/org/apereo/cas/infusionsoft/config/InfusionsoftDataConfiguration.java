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
@EnableJpaRepositories("org.apereo.cas.infusionsoft")
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

}
