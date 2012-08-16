package com.infusionsoft.cas.types;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String passwordRecoveryCode;
    private boolean enabled;
    private Set<UserAccount> accounts = new HashSet<UserAccount>();
    private List<UserPassword> passwords = new ArrayList<UserPassword>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "first_name", length = 60)
    @Length(min = 1, max = 60)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name", length = 60)
    @Length(min = 1, max = 60)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "username", unique = true, length = 120)
    @Email
    @Length(min = 8, max = 120)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password_recovery_code", length = 32, nullable = true)
    public String getPasswordRecoveryCode() {
        return passwordRecoveryCode;
    }

    public void setPasswordRecoveryCode(String passwordRecoveryCode) {
        this.passwordRecoveryCode = passwordRecoveryCode;
    }

    @Column(name = "enabled")
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @OneToMany(targetEntity = UserAccount.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<UserAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<UserAccount> accounts) {
        this.accounts = accounts;
    }

    @OneToMany(targetEntity = UserPassword.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<UserPassword> getPasswords() {
		return passwords;
	}

	public void setPasswords(List<UserPassword> passwords) {
		this.passwords = passwords;
	}
}
