package com.infusionsoft.cas.api.domain;

import com.infusionsoft.cas.domain.Authority;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.support.AppHelper;
import org.codehaus.jackson.annotate.JsonTypeName;

import java.util.Set;

/**
 * Represents a user
 */
@JsonTypeName("User")
public class UserDTO {
    private long casGlobalId;
    private String username;
    private String displayName;
    private String firstName;
    private String lastName;
    private UserAccountDTO[] linkedApps;
    private String[] authorities;

    public UserDTO() {
        // For de-serialization
    }

    public UserDTO(User user, AppHelper appHelper) {
        casGlobalId = user.getId();
        username = user.getUsername();
        displayName = user.getFirstName() + " " + user.getLastName();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        linkedApps = UserAccountDTO.convertFromCollection(user.getAccounts(), appHelper);

        Set<Authority> userAuthorities = user.getAuthorities();
        authorities = new String[userAuthorities.size()];
        int i = 0;
        for (Authority authority : userAuthorities) {
            authorities[i++] = authority.getAuthority();
        }
    }

    public long getCasGlobalId() {
        return casGlobalId;
    }

    public void setCasGlobalId(long casGlobalId) {
        this.casGlobalId = casGlobalId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserAccountDTO[] getLinkedApps() {
        return linkedApps;
    }

    public void setLinkedApps(UserAccountDTO[] linkedApps) {
        this.linkedApps = linkedApps;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }
}
