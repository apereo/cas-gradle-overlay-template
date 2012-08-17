package com.infusionsoft.cas.types;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "login_attempt")
public class LoginAttempt {
    private Long id;
    private String username;
    private Date dateAttempted;
    boolean success;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "username", length = 120)
    @Length(min = 1, max = 120)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "date")
    public Date getDateAttempted() {
        return dateAttempted;
    }

    public void setDateAttempted(Date dateAttempted) {
        this.dateAttempted = dateAttempted;
    }

    @Column(name = "success")
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
