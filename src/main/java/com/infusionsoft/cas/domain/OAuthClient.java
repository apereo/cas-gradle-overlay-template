package com.infusionsoft.cas.domain;

import org.hibernate.validator.constraints.Length;
import org.jasig.cas.services.AbstractRegisteredService;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.RegisteredServiceImpl;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "oauth_client", uniqueConstraints =
        {@UniqueConstraint(name="oauth_client_id", columnNames = {"client_id"})}
)
public class OAuthClient {
    private Long id;
    private String clientId;
    private String clientSecret;
    private RegisteredService registeredService;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "client_id", length = 100)
    @NotNull
    @Length(min = 1, max = 100, message = "{oauth.error.client.id.length}")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Column(name = "client_secret", length = 100)
    @NotNull
    @Length(min = 1, max = 100, message = "{oauth.error.client.secret.length}")
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @NotNull
    @ManyToOne(targetEntity = AbstractRegisteredService.class, optional = false)
    public RegisteredService getRegisteredService() {
        return registeredService;
    }

    public void setRegisteredService(RegisteredService registeredService) {
        this.registeredService = registeredService;
    }
}
