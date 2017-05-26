package org.apereo.cas.infusionsoft.domain;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "User")
@Table(name = "user")
public class User implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String passwordRecoveryCode;
    private DateTime passwordRecoveryCodeCreatedTime;
    private String ipAddress;
    private boolean enabled;
    private Set<Authority> authorities = new HashSet<>();
    private Set<UserAccount> accounts = new HashSet<>();
    private List<UserPassword> passwords = new ArrayList<>();
    private List<SecurityQuestionResponse> securityQuestionResponses = new ArrayList<>();

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
    @Deprecated
    public String getFirstName() {
        return firstName;
    }

    @Deprecated
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name", length = 60)
    @Length(min = 1, max = 60, message = "{user.error.lastName.length}")
    @NotBlank(message = "{user.error.lastName.blank}")
    @SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
    @Deprecated
    public String getLastName() {
        return lastName;
    }

    @Deprecated
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NotNull
    @Column(name = "username", unique = true, length = 120, nullable = false)
    @Email(message = "{user.error.email.invalid}")
    @Length(min = 8, max = 120, message = "{user.error.email.length}")
    @NotBlank(message = "{user.error.email.blank}")
    @Deprecated
    public String getUsername() {
        return username;
    }

    @Deprecated
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

    @Column(name = "ip_address", length = 32, nullable = true)
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @NotNull
    @Column(name = "enabled", nullable = false)
    @Deprecated
    public boolean isEnabled() {
        return enabled;
    }

    @Deprecated
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @ManyToMany(targetEntity = Authority.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
    @Deprecated
    public Set<Authority> getAuthorities() {
        return authorities;
    }

    @Deprecated
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

    public String toString() {
        return username;
    }
}
