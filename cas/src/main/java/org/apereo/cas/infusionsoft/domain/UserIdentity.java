package org.apereo.cas.infusionsoft.domain;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "user_identity", uniqueConstraints = {@UniqueConstraint(columnNames = {"external_id"})})
public class UserIdentity implements Serializable {
    private Long id;
    private User user;
    private String externalId;

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

    @NotNull
    @Column(name = "externalId", length = 255, nullable = false)
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String toString() {
        return StringUtils.join(
                "ID: ", id, ", ",
                "user: ", user, ", ",
                "externalId: ", externalId, ", "
        );
    }
}
