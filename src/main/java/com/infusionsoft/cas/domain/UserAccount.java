package com.infusionsoft.cas.domain;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "user_account", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "app_name", "app_type"}), @UniqueConstraint(name = "app_type_name_username", columnNames = {"app_type", "app_name", "app_username"})})
public class UserAccount implements Serializable {
    private Long id;
    private User user;
    private String alias;
    private AppType appType;
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
    @SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
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
    @Enumerated(EnumType.STRING)
    public AppType getAppType() {
        return appType;
    }

    public void setAppType(AppType appType) {
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

    @Override
    public String toString() {
        return StringUtils.join(
                "ID: ", id, ", ",
                "user: ", user, ", ",
                "alias: ", alias, ", ",
                "appType: ", appType, ", ",
                "appName: ", appName, ", ",
                "appUsername: ", appUsername, ", ",
                "disabled: ", disabled
        );
    }
}
