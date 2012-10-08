package com.infusionsoft.cas.types;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * An app that has been successfully migrated to CAS. Users that are referred from apps that have an entry in this
 * table will bypass the migration flow. Mostly, this is used to keep track of apps that were created after CAS
 * launched and therefore we know they have no "legacy" users.
 */
@Entity
@Table(name = "migrated_app", uniqueConstraints = {@UniqueConstraint(columnNames = {"app_name", "app_type"})})
public class MigratedApp {
    private Long id;
    private String appName;
    private String appType;
    private Date dateMigrated;

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

    @Column(name = "app_type", length = 255)
    @NotNull
    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    @Column(name = "date_migrated")
    @NotNull
    public Date getDateMigrated() {
        return dateMigrated;
    }

    public void setDateMigrated(Date dateMigrated) {
        this.dateMigrated = dateMigrated;
    }
}
