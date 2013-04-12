package com.infusionsoft.cas.domain;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Authority")
@Table(name = "authority", uniqueConstraints = {@UniqueConstraint(columnNames = {"authority"})})
public class Authority implements Serializable, GrantedAuthority {
    private Long id;
    private String authority;
    private Set<User> users = new HashSet<User>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column
    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @ManyToMany(targetEntity = User.class, mappedBy = "authorities")
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String toString() {
        return authority;
    }
}
