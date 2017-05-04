package org.apereo.cas.infusionsoft.domain;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "oauth_service_config", uniqueConstraints =
        {@UniqueConstraint(name="oauth_service_config_name",columnNames = {"name"})}
)
public class OAuthServiceConfig {
    private Long id;
    private String name;
    private String serviceKey;
    private String description;
    private Boolean allowAnonymous = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", length = 100)
    @NotNull
    @Length(min = 1, max = 100, message = "{oauth.error.service.config.name.length}")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "service_key", length = 100)
    @NotNull
    @Length(min = 1, max = 100, message = "{oauth.error.service.config.key.length}")
    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    @Column(name = "description", length = 200)
    @NotNull
    @Length(min = 1, max = 200, message = "{oauth.error.service.config.description.length}")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "allow_anonymous")
    @NotNull
    public Boolean getAllowAnonymous() {
        return allowAnonymous;
    }

    public void setAllowAnonymous(Boolean allowAnonymous) {
        this.allowAnonymous = allowAnonymous;
    }
}
