package com.infusionsoft.cas.domain;

import com.sun.istack.NotNull;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * An entry into our audit log.
 */
@Entity(name = "AuditEntry")
@Table(name = "audit_log")
public class AuditEntry {
    private Long id;
    private AuditEntryType type;
    private String serviceBaseUrl;
    private Long serviceId;
    private String username;
    private Long userId;
    private DateTime date;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "date", nullable = true)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Index(name = "audit_log_date_index", columnNames = { "date" })
    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    @Column(name = "type", length = 30)
    @NotNull
    @Enumerated(EnumType.STRING)
    public AuditEntryType getType() {
        return type;
    }

    public void setType(AuditEntryType type) {
        this.type = type;
    }

    @Column(name = "service_base_url", length = 100, nullable = true)
    public String getServiceBaseUrl() {
        return serviceBaseUrl;
    }

    public void setServiceBaseUrl(String serviceBaseUrl) {
        this.serviceBaseUrl = serviceBaseUrl;
    }

    @Column(name = "service_id", nullable = true)
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "username", length = 120, nullable = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "user_id", nullable = true)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
