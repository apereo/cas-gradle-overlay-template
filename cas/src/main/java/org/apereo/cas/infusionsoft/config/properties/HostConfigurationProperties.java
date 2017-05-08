package org.apereo.cas.infusionsoft.config.properties;

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
        StringBuilder url = new StringBuilder(protocol + "://" + domain);

        if ("http".equals(protocol) && port != 80) {
            url.append(":").append(port);
        } else if ("https".equals(protocol) && port != 443) {
            url.append(":").append(port);
        }
        return url.toString();
    }

}
