package com.infusionsoft.cas.oauth.dto;

/**
 * A class that represents a OAuth Client Application
 */
public class OAuthApplication {
    private String name;
    private String description;
    private String developedBy;

    public OAuthApplication(String name, String description, String developedBy) {
        this.name = name;
        this.description = description;
        this.developedBy = developedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDevelopedBy() {
        return developedBy;
    }

    public void setDevelopedBy(String developedBy) {
        this.developedBy = developedBy;
    }
}
