package org.apereo.cas.infusionsoft.services;

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
     * Builds a base URL to a CRM app.
     *
     * @param appName appName
     * @return redirect url
     */
    public String buildCrmUrl(String appName) {
        StringBuilder url = new StringBuilder(hostProperties.getProtocol() + "://" + buildCrmHostName(appName));

        if (hostProperties.getProtocol().equals("http") && hostProperties.getPort() != 80) {
            url.append(":").append(hostProperties.getPort());
        } else if (hostProperties.getProtocol().equals("https") && hostProperties.getPort() != 443) {
            url.append(":").append(hostProperties.getPort());
        }

        return url.toString();
    }

    @Deprecated
    private String buildCrmHostName(String appName) {
        return appName + "." + hostProperties.getDomain();
    }

    public String getLogoUrl(String appName) {
        return buildCrmUrl(appName) + "/Logo?logo=weblogo";
    }

}
