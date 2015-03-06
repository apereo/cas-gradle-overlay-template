package com.infusionsoft.cas.domain;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "User")
@Table(name = "user")
public class User implements Serializable, UserDetails {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String passwordRecoveryCode;
    private DateTime passwordRecoveryCodeCreatedTime;
    private boolean enabled;
    private Set<Authority> authorities = new HashSet<Authority>();
    private Set<UserAccount> accounts = new HashSet<UserAccount>();
    private List<UserPassword> passwords = new ArrayList<UserPassword>();
    private List<SecurityQuestionResponse> securityQuestionResponses = new ArrayList<SecurityQuestionResponse>();

    //Spring Security UserDetails fields
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "first_name", length = 60)
    @Length(min = 1, max = 60, message = "{user.error.firstName.length}")
    @NotBlank(message = "{user.error.firstName.blank}")
    @SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name", length = 60)
    @Length(min = 1, max = 60, message = "{user.error.lastName.length}")
    @NotBlank(message = "{user.error.lastName.blank}")
    @SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NotNull
    @Column(name = "username", unique = true, length = 120, nullable = false)
    @Email(message = "{user.error.email.invalid}")
    @Length(min = 8, max = 120, message = "{user.error.email.length}")
    @NotBlank(message = "{user.error.email.blank}")
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

    @Column(name = "password_recovery_code_created_time", nullable = true)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getPasswordRecoveryCodeCreatedTime() {
        return passwordRecoveryCodeCreatedTime;
    }

    public void setPasswordRecoveryCodeCreatedTime(DateTime passwordRecoveryCodeCreatedTime) {
        this.passwordRecoveryCodeCreatedTime = passwordRecoveryCodeCreatedTime;
    }

    @NotNull
    @Column(name = "enabled", nullable = false)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    @ManyToMany(targetEntity = Authority.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    @OneToMany(targetEntity = UserAccount.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    public Set<UserAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<UserAccount> accounts) {
        this.accounts = accounts;
    }

    @OneToMany(targetEntity = UserPassword.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    public List<UserPassword> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<UserPassword> passwords) {
        this.passwords = passwords;
    }

    @OneToMany(targetEntity = SecurityQuestionResponse.class, cascade = CascadeType.ALL, mappedBy = "user")
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<SecurityQuestionResponse> getSecurityQuestionResponses() {
        return securityQuestionResponses;
    }

    public void setSecurityQuestionResponses(List<SecurityQuestionResponse> securityQuestionResponses) {
        this.securityQuestionResponses = securityQuestionResponses;
    }

    @Override
    @Transient
    public String getPassword() {
        return password;
    }

    /**
     * @deprecated This is only to be used in the CasUserDetailsService to populate the encoded password.  Do not use for anything else.
     */
    @Transient
    @Deprecated
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Transient
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Transient
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Transient
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public String toString() {
        return username;
    }
}
