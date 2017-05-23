package org.apereo.cas.infusionsoft.services;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.infusionsoft.config.properties.HostConfigurationProperties;

/**
 * Service for communicating with the Infusionsoft CRM (aka "the app").
 */
public class CrmService {

    private HostConfigurationProperties hostProperties;

    public CrmService(HostConfigurationProperties hostProperties) {
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

        return url.toString();
    }

}
