package com.infusionsoft.cas.types;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "user_account", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "app_name"})})
public class UserAccount implements Serializable {
    private Long id;
    private User user;
    private String appType;
    private String appName;
    private String appUsername;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = User.class)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

}
