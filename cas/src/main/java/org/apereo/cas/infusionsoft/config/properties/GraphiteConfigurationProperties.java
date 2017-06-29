package org.apereo.cas.infusionsoft.config.properties;

public class GraphiteConfigurationProperties extends HostConfigurationProperties {

    private String apiKey;
    private boolean enabled = false;
    private String prefix;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
