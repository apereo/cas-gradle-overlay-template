package org.apereo.cas.infusionsoft.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user_password")
public class UserPassword implements Serializable {
    private Long id;
    private User user;
    private String passwordEncoded;
    private String passwordEncodedMD5;
    private Date dateCreated;
    private boolean active = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "password_encoded", length = 255)
    public String getPasswordEncoded() {
        return passwordEncoded;
    }

    public void setPasswordEncoded(String passwordEncoded) {
        this.passwordEncoded = passwordEncoded;
    }

    @Column(name = "password_encoded_md5", length = 255)
    public String getPasswordEncodedMD5() {
        return passwordEncodedMD5;
    }

    public void setPasswordEncodedMD5(String passwordEncodedMD5) {
        this.passwordEncodedMD5 = passwordEncodedMD5;
    }

    @Column(name = "date_created")
    @NotNull
    public Date getDateCreated() {
        return new Date(dateCreated.getTime());
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = new Date(dateCreated.getTime());
    }

    @Column(name = "active")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
