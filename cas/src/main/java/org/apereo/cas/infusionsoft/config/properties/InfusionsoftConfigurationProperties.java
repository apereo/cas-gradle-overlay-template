package org.apereo.cas.infusionsoft.config.properties;

import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.configuration.model.core.authentication.PasswordEncoderProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

@ConfigurationProperties("infusionsoft")
public class InfusionsoftConfigurationProperties {

    @NestedConfigurationProperty
    private AccountCentralConfigurationProperties accountCentral = new AccountCentralConfigurationProperties();

    private long auditEntryMaxAge = 86400000 * 7; // default to 7 days

    @NestedConfigurationProperty
    private HostConfigurationProperties community = new HostConfigurationProperties();

    @NestedConfigurationProperty
    private HostConfigurationProperties crm = new HostConfigurationProperties();

    @NestedConfigurationProperty
    private HostConfigurationProperties customerhub = new HostConfigurationProperties();

    @NestedConfigurationProperty
    private UsernamePasswordCredential customerHubApi;

    @NestedConfigurationProperty
    private InfusionsoftJpaConfigurationProperties jpa = new InfusionsoftJpaConfigurationProperties();

    private long loginAttemptMaxAge = 86400000; // default to 1 day

    @NestedConfigurationProperty
    private HostConfigurationProperties mail = new HostConfigurationProperties();

    @NestedConfigurationProperty
    private HostConfigurationProperties marketplace = new HostConfigurationProperties();

    private int numSecurityQuestionsRequired;

    @NestedConfigurationProperty
    private PasswordEncoderProperties passwordEncoder = new PasswordEncoderProperties();

    private List<String> supportPhoneNumbers;

    public AccountCentralConfigurationProperties getAccountCentral() {
        return accountCentral;
    }

    public void setAccountCentral(AccountCentralConfigurationProperties accountCentral) {
        this.accountCentral = accountCentral;
    }

    public long getAuditEntryMaxAge() {
        return auditEntryMaxAge;
    }

    public void setAuditEntryMaxAge(long auditEntryMaxAge) {
        this.auditEntryMaxAge = auditEntryMaxAge;
    }

    public HostConfigurationProperties getCommunity() {
        return community;
    }

    public void setCommunity(HostConfigurationProperties community) {
        this.community = community;
    }

    public HostConfigurationProperties getCrm() {
        return crm;
    }

    public void setCrm(HostConfigurationProperties crm) {
        this.crm = crm;
    }

    public HostConfigurationProperties getCustomerhub() {
        return customerhub;
    }

    public void setCustomerhub(HostConfigurationProperties customerhub) {
        this.customerhub = customerhub;
    }

    public UsernamePasswordCredential getCustomerHubApi() {
        return customerHubApi;
    }

    public void setCustomerHubApi(UsernamePasswordCredential customerHubApi) {
        this.customerHubApi = customerHubApi;
    }

    public InfusionsoftJpaConfigurationProperties getJpa() {
        return jpa;
    }

    public void setJpa(InfusionsoftJpaConfigurationProperties jpa) {
        this.jpa = jpa;
    }

    public long getLoginAttemptMaxAge() {
        return loginAttemptMaxAge;
    }

    public void setLoginAttemptMaxAge(long loginAttemptMaxAge) {
        this.loginAttemptMaxAge = loginAttemptMaxAge;
    }

    public HostConfigurationProperties getMail() {
        return mail;
    }

    public void setMail(HostConfigurationProperties mail) {
        this.mail = mail;
    }

    public HostConfigurationProperties getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(HostConfigurationProperties marketplace) {
        this.marketplace = marketplace;
    }

    public int getNumSecurityQuestionsRequired() {
        return numSecurityQuestionsRequired;
    }

    public void setNumSecurityQuestionsRequired(int numSecurityQuestionsRequired) {
        this.numSecurityQuestionsRequired = numSecurityQuestionsRequired;
    }

    public PasswordEncoderProperties getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoderProperties passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public List<String> getSupportPhoneNumbers() {
        return supportPhoneNumbers;
    }

    public void setSupportPhoneNumbers(List<String> supportPhoneNumbers) {
        this.supportPhoneNumbers = supportPhoneNumbers;
    }

}
