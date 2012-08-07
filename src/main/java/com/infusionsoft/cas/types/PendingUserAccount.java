package com.infusionsoft.cas.types;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "pending_user_account", uniqueConstraints = {@UniqueConstraint(columnNames = {"app_name", "app_username"})})
public class PendingUserAccount implements Serializable {
    private Long id;
    private String appType;
    private String appName;
    private String appUsername;
    private String registrationCode;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "app_name", length = 255)
    @NotNull
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Column(name = "app_username", length = 255)
    @NotNull
    public String getAppUsername() {
        return appUsername;
    }

    public void setAppUsername(String appUsername) {
        this.appUsername = appUsername;
    }

    @Column(name = "app_type", length = 255)
    @NotNull
    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    @Column(name = "registration_code", length = 255, unique = true)
    @NotNull
    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }
}
