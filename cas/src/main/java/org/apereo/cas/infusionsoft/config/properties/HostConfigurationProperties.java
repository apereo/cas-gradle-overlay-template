package org.apereo.cas.infusionsoft.config.properties;

import org.apache.commons.lang3.StringUtils;

public class HostConfigurationProperties {
    private String domain;
    private String protocol = "https";
    private int port = 443;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUrl() {
        return getUrl(null);
    }

    public String getUrl(String appName) {
        StringBuilder url = new StringBuilder(protocol);
        url.append("://");
        if (StringUtils.isNotBlank(appName)) {
            url.append(appName).append(".");
        }
        url.append(domain);

        if ("http".equals(protocol) && port != 80) {
            url.append(":").append(port);
        } else if ("https".equals(protocol) && port != 443) {
            url.append(":").append(port);
        }
        return url.toString();
    }

}
