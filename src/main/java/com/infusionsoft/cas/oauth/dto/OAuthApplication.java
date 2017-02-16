package com.infusionsoft.cas.oauth.dto;

/**
 * A class that represents a OAuth Client Application
 */
public class OAuthApplication {
    private String id;
    private String name;
    private String description;
    private String developedBy;
    private String developedByUsername;

    public OAuthApplication(String id, String name, String description, String developedBy, String developedByUsername) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.developedBy = developedBy;
        this.developedByUsername = developedByUsername;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDevelopedByUsername() {
        return developedByUsername;
    }

    public void setDevelopedByUsername(String developedByUsername) {
        this.developedByUsername = developedByUsername;
    }
}
