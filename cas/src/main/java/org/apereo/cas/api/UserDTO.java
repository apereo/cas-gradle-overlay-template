package org.apereo.cas.api;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apereo.cas.infusionsoft.domain.Authority;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.apereo.cas.infusionsoft.support.UserAccountTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Represents a user
 */
@JsonTypeName("User")
@Deprecated
public class UserDTO {
    private long globalUserId;
    private String username;
    private String displayName;
    private String firstName;
    private String lastName;
    private UserAccountDTO[] linkedApps;
    private String[] authorities;

    public UserDTO() {
        // For de-serialization
    }

    public UserDTO(User user, List<UserAccount> accounts, UserAccountTransformer userAccountTransformer) {
        globalUserId = user.getId();
        username = user.getUsername();
        displayName = user.getFirstName() + " " + user.getLastName();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        linkedApps = UserAccountDTO.convertFromCollection(accounts, userAccountTransformer);

        Set<Authority> userAuthorities = user.getAuthorities();
        authorities = new String[userAuthorities.size()];
        int i = 0;
        for (Authority authority : userAuthorities) {
            authorities[i++] = authority.getAuthority();
        }
    }

    // For backwards compatibility: TODO: remove once all apps use globalUserId instead of casGlobalId
    public long getCasGlobalId() {
        return getGlobalUserId();
    }

    public long getGlobalUserId() {
        return globalUserId;
    }

    public void setGlobalUserId(long globalUserId) {
        this.globalUserId = globalUserId;
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
        return linkedApps == null ? null : Arrays.copyOf(linkedApps, linkedApps.length);
    }

    public void setLinkedApps(UserAccountDTO[] linkedApps) {
        this.linkedApps = linkedApps == null ? null : Arrays.copyOf(linkedApps, linkedApps.length);
    }

    public String[] getAuthorities() {
        return authorities == null ? null : Arrays.copyOf(authorities, authorities.length);
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities == null ? null : Arrays.copyOf(authorities, authorities.length);
    }
}
