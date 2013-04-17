package com.infusionsoft.cas.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "user_account", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "app_name", "app_type"}), @UniqueConstraint(name = "app_type_name_username", columnNames = {"app_type", "app_name", "app_username"})})
public class UserAccount implements Serializable {
    private Long id;
    private User user;
    private String alias;
    private String appType;
    private String appName;
    private String appUsername;
    private boolean disabled = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @ManyToOne(targetEntity = User.class, optional = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "alias", length = 60)
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @NotNull
    @Column(name = "app_name", length = 255, nullable = false)
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @NotNull
    @Column(name = "app_username", length = 255, nullable = false)
    public String getAppUsername() {
        return appUsername;
    }

    public void setAppUsername(String appUsername) {
        this.appUsername = appUsername;
    }

    @NotNull
    @Column(name = "app_type", length = 255, nullable = false)
    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    @NotNull
    @Column(name = "disabled", nullable = false)
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
