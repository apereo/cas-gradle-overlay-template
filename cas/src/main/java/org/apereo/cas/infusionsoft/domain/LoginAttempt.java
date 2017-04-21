package org.apereo.cas.infusionsoft.domain;

import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "login_attempt")
public class LoginAttempt implements Serializable {
    private Long id;
    private String username;
    private Date dateAttempted;
    private LoginAttemptStatus status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "username", length = 120)
    @Length(min = 1, max = 120, message = "{user.error.email.length}")
    @Index(name = "login_attempt_username_index", columnNames = { "username" })
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "date")
    @Index(name = "login_attempt_date_index", columnNames = { "date_attempted" })
    public Date getDateAttempted() {
        return new Date(dateAttempted.getTime());
    }

    public void setDateAttempted(Date dateAttempted) {
        this.dateAttempted = new Date(dateAttempted.getTime());
    }

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
    public LoginAttemptStatus getStatus() {
        return status;
    }

    public void setStatus(LoginAttemptStatus loginAttemptStatus) {
        this.status = loginAttemptStatus;
    }
}
