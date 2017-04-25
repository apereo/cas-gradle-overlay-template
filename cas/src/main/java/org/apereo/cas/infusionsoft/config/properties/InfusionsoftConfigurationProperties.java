package org.apereo.cas.infusionsoft.config.properties;

import org.apereo.cas.configuration.model.core.authentication.PasswordEncoderProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

@ConfigurationProperties("infusionsoft")
public class InfusionsoftConfigurationProperties {

    @NestedConfigurationProperty
    private AccountCentralConfigurationProperties accountCentral = new AccountCentralConfigurationProperties();

    @NestedConfigurationProperty
    private InfusionsoftJpaConfigurationProperties jpa = new InfusionsoftJpaConfigurationProperties();

    @NestedConfigurationProperty
    private HostConfigurationProperties customerhub = new HostConfigurationProperties();

    @NestedConfigurationProperty
    private HostConfigurationProperties crm = new HostConfigurationProperties();

    @NestedConfigurationProperty
    private HostConfigurationProperties mail = new HostConfigurationProperties();

    @NestedConfigurationProperty
    private HostConfigurationProperties marketplace = new HostConfigurationProperties();

    @NestedConfigurationProperty
    private PasswordEncoderProperties passwordEncoder = new PasswordEncoderProperties();

    private List<String> supportPhoneNumbers;

    public AccountCentralConfigurationProperties getAccountCentral() {
        return accountCentral;
    }

    public void setAccountCentral(AccountCentralConfigurationProperties accountCentral) {
        this.accountCentral = accountCentral;
    }

    public InfusionsoftJpaConfigurationProperties getJpa() {
        return jpa;
    }

    public void setJpa(InfusionsoftJpaConfigurationProperties jpa) {
        this.jpa = jpa;
    }

    public HostConfigurationProperties getMail() {
        return mail;
    }

    public void setMail(HostConfigurationProperties mail) {
        this.mail = mail;
    }

    public PasswordEncoderProperties getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoderProperties passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public HostConfigurationProperties getCustomerhub() {
        return customerhub;
    }

    public void setCustomerhub(HostConfigurationProperties customerhub) {
        this.customerhub = customerhub;
    }

    public HostConfigurationProperties getCrm() {
        return crm;
    }

    public void setCrm(HostConfigurationProperties crm) {
        this.crm = crm;
    }

    public HostConfigurationProperties getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(HostConfigurationProperties marketplace) {
        this.marketplace = marketplace;
    }

    public List<String> getSupportPhoneNumbers() {
        return supportPhoneNumbers;
    }

    public void setSupportPhoneNumbers(List<String> supportPhoneNumbers) {
        this.supportPhoneNumbers = supportPhoneNumbers;
    }
}
