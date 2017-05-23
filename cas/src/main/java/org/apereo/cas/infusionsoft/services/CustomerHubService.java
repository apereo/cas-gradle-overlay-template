package org.apereo.cas.infusionsoft.services;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.infusionsoft.config.properties.HostConfigurationProperties;

/**
 * Service for communicating back and forth with CustomerHub.
 */
public class CustomerHubService {

    private HostConfigurationProperties hostProperties;

    public CustomerHubService(HostConfigurationProperties hostProperties) {
        this.hostProperties = hostProperties;
    }

    /**
     * Builds a URL where users of this app should be sent after login.
     *
     * @param appName appName
     * @return redirect url
     */
    public String buildUrl(String appName) {
        StringBuilder url = new StringBuilder(hostProperties.getProtocol());
        url.append("://").append(appName).append(".").append(hostProperties.getDomain());

        if (StringUtils.equals("http", hostProperties.getProtocol()) && hostProperties.getPort() != 80) {
            url.append(":").append(hostProperties.getPort());
        } else if (StringUtils.equals("https", hostProperties.getProtocol()) && hostProperties.getPort() != 443) {
            url.append(":").append(hostProperties.getPort());
        }

        url.append("/admin");
        return url.toString();
    }

}
