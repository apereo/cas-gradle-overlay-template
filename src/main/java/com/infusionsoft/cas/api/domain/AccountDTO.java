package com.infusionsoft.cas.api.domain;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.support.AppHelper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonTypeName;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents an account record.  Used when the account is not nested within a {@link UserDTO} object, so the user
 * information ({@link #getInfusionsoftId()}}, {@link #getCasGlobalId()}) must be included.  If the user information
 * is not needed, use {@link UserAccountDTO}.
 */
@JsonTypeName("Account")
public class AccountDTO {
    private AppType appType;
    private String appName;
    private String appUsername;
    private String appAlias;
    private String appUrl;
    private String infusionsoftId;
    private Long casGlobalId;

    public static Set<AccountDTO> convertFromCollection(final Collection<UserAccount> accountCollection, AppHelper appHelper) {
        Set<AccountDTO> accounts = new LinkedHashSet<AccountDTO>(accountCollection.size());
        for (UserAccount userAccount : accountCollection) {
            accounts.add(new AccountDTO(userAccount, appHelper));
        }
        return accounts;
    }

    public AccountDTO() {
        // For de-serialization
    }

    public AccountDTO(UserAccount userAccount, AppHelper appHelper) {
        this.appType = userAccount.getAppType();
        this.appName = userAccount.getAppName();
        this.appUsername = userAccount.getAppUsername();
        this.appAlias = StringUtils.defaultIfEmpty(userAccount.getAlias(), userAccount.getAppName());
        this.appUrl = appHelper.buildAppUrl(userAccount.getAppType(), userAccount.getAppName());
        User user = userAccount.getUser();
        if (user != null) {
            this.infusionsoftId = user.getUsername();
            this.casGlobalId = user.getId();
        } else {
            this.infusionsoftId = null;
            this.casGlobalId = null;
        }
    }

    public AppType getAppType() {
        return appType;
    }

    public void setAppType(AppType appType) {
        this.appType = appType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppUsername() {
        return appUsername;
    }

    public void setAppUsername(String appUsername) {
        this.appUsername = appUsername;
    }

    public String getAppAlias() {
        return appAlias;
    }

    public void setAppAlias(String appAlias) {
        this.appAlias = appAlias;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getInfusionsoftId() {
        return infusionsoftId;
    }

    public void setInfusionsoftId(String infusionsoftId) {
        this.infusionsoftId = infusionsoftId;
    }

    public Long getCasGlobalId() {
        return casGlobalId;
    }

    public void setCasGlobalId(Long casGlobalId) {
        this.casGlobalId = casGlobalId;
    }
}
