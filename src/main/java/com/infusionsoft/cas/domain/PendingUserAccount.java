package com.infusionsoft.cas.domain;

import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "pending_user_account", uniqueConstraints = {@UniqueConstraint(columnNames = {"app_name", "app_type", "app_username"})})
public class PendingUserAccount implements Serializable {
    private Long id;
    private String registrationCode;
    private AppType appType;
    private String appName;
    private String appUsername;
    private String firstName;
    private String lastName;
    private String email;
    private boolean passwordVerificationRequired = true;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Enumerated(EnumType.STRING)
    public AppType getAppType() {
        return appType;
    }

    public void setAppType(AppType appType) {
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

    @Column(name = "first_name", length = 60)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name", length = 60)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "email", length = 255)
    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "password_verification_required")
    public boolean isPasswordVerificationRequired() {
        return passwordVerificationRequired;
    }

    public void setPasswordVerificationRequired(boolean passwordVerificationRequired) {
        this.passwordVerificationRequired = passwordVerificationRequired;
    }
}
