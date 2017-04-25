package org.apereo.cas.infusionsoft.services;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.infusionsoft.config.properties.HostConfigurationProperties;

/**
 * Service for communicating back and forth with CustomerHub.
 */
public class CustomerHubService {

    private HostConfigurationProperties hostConfigurationProperties;

    public CustomerHubService(HostConfigurationProperties hostConfigurationProperties) {
        this.hostConfigurationProperties = hostConfigurationProperties;
    }

    /**
     * Builds a URL where users of this app should be sent after login.
     */
    public String buildUrl(String appName) {
        return buildBaseUrl(appName) + "/admin";
    }

    /**
     * Builds a base URL for web services calls and what-not.
     */
    private String buildBaseUrl(String appName) {
        StringBuilder baseUrl = new StringBuilder(hostConfigurationProperties.getProtocol() + "://" + appName + "." + hostConfigurationProperties.getDomain());

        if (StringUtils.equals("http", hostConfigurationProperties.getProtocol()) && hostConfigurationProperties.getPort() != 80) {
            baseUrl.append(":").append(hostConfigurationProperties.getPort());
        } else if (StringUtils.equals("https", hostConfigurationProperties.getProtocol()) && hostConfigurationProperties.getPort() != 443) {
            baseUrl.append(":").append(hostConfigurationProperties.getPort());
        }

        return baseUrl.toString();
    }
}
