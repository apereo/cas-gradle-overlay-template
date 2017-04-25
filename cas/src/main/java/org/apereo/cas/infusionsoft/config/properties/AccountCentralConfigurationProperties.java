package org.apereo.cas.infusionsoft.config.properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("accountCentral")
public class AccountCentralConfigurationProperties {

    @NestedConfigurationProperty
    private HostConfigurationProperties host = new HostConfigurationProperties();

    private String registrationPath;

    public HostConfigurationProperties getHost() {
        return host;
    }

    public void setHost(HostConfigurationProperties host) {
        this.host = host;
    }

    public String getRegistrationPath() {
        return registrationPath;
    }

    public void setRegistrationPath(String registrationPath) {
        this.registrationPath = registrationPath;
    }

    public String getRegistrationUrl() {
        return StringUtils.join(host.getProtocol(), host.getDomain(), host.getPort(), registrationPath);
    }
}
