package com.infusionsoft.cas.api.domain;

import com.infusionsoft.cas.domain.Authority;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.support.AppHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This is a DTO for the User API endpoint. It is different than UserDTO so the format can be distinct.
 */
@SuppressWarnings({"unused", "ReturnOfCollectionOrArrayField"})
public class UserResource {
    private String id;
    private String username;
    private String displayName;
    private boolean enabled;
    private List<UserAccountDTO> accounts;
    private List<String> roles;


    public UserResource(User user, List<UserAccount> accounts, AppHelper appHelper) {
        if (user == null) {
            throw new NullPointerException("User can not be null");
        }
        if (user.getId() == null) {
            throw new NullPointerException("User ID can not be null");
        }
        this.id = Long.toString(user.getId(), 10);
        this.username = user.getUsername();
        this.displayName = user.getFirstName() + " " + user.getLastName();
        this.enabled = user.isEnabled();

        this.accounts = new ArrayList<UserAccountDTO>(accounts.size());
        for (UserAccount userAccount : accounts) {
            this.accounts.add(new UserAccountDTO(userAccount, appHelper));
        }

        final Set<Authority> userAuthorities = user.getAuthorities();
        this.roles = new ArrayList<String>(userAuthorities.size());
        for (Authority authority : userAuthorities) {
            this.roles.add(authority.getAuthority());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<UserAccountDTO> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<UserAccountDTO> accounts) {
        this.accounts = accounts;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
