package com.infusionsoft.cas.oauth.dto;

import java.util.Set;

/**
 * A class that represents a OAuth Client Application
 */
public class OAuthApplication {
    private String name;
    private String description;
    private String developedBy;
    private Set<String> roles;

    public OAuthApplication(String name, String description, String developedBy, Set<String> roles) {
        this.name = name;
        this.description = description;
        this.developedBy = developedBy;
        this.roles = roles;
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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
