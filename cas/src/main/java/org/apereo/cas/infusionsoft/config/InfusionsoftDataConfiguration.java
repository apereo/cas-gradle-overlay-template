package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jpa.JpaConfigDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.MessageSource;
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

import static org.apereo.cas.configuration.support.Beans.newEntityManagerFactoryBean;
import static org.apereo.cas.configuration.support.Beans.newHibernateJpaVendorAdapter;
import static org.apereo.cas.configuration.support.Beans.newHickariDataSource;

@Configuration
@EnableConfigurationProperties({CasConfigurationProperties.class, InfusionsoftConfigurationProperties.class})
@EnableJpaRepositories("org.apereo.cas.infusionsoft")
@EnableTransactionManagement(proxyTargetClass = true)
public class InfusionsoftDataConfiguration {

    @Autowired
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CasConfigurationProperties casProperties;

//    @Bean
//    public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
//        return new PersistenceAnnotationBeanPostProcessor();
//    }

    @RefreshScope
    @Bean
    public HibernateJpaVendorAdapter jpaInfusionsoftVendorAdapter() {
        return newHibernateJpaVendorAdapter(casProperties.getJdbc());
    }

    @Bean
    public String[] jpaInfusionsoftPackagesToScan() {
        return new String[]{"org.apereo.cas.infusionsoft.domain"};
    }

//    @Bean
//    @DependsOn("messageSource")
//    LocalValidatorFactoryBean validator() {
//        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
//        localValidatorFactoryBean.setValidationMessageSource(messageSource);
//
//        return localValidatorFactoryBean;
//    }

    @Lazy
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        return newEntityManagerFactoryBean(
                new JpaConfigDataHolder(
                        jpaInfusionsoftVendorAdapter(),
                        "jpaInfusionsoftContext",
                        jpaInfusionsoftPackagesToScan(),
                        dataSource()),
                infusionsoftConfigurationProperties.getJpa());
    }

    @Autowired
    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") final EntityManagerFactory emf) {
        final JpaTransactionManager mgmr = new JpaTransactionManager();
        mgmr.setEntityManagerFactory(emf);
        return mgmr;
    }

    @RefreshScope
    @Bean
    public DataSource dataSource() {
        return newHickariDataSource(infusionsoftConfigurationProperties.getJpa());
    }
}
