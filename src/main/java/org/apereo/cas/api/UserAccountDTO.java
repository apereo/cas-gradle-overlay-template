package org.apereo.cas.api;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * Represents an account record.  Used when the account is nested within a {@link UserDTO} object, so the user
 * information is not included.  If the user information is needed on a per-account basis, use {@link AccountDTO}.
 */
@JsonTypeName("UserAccount")
public class UserAccountDTO {
    private String appType; //TODO: change back to AppType enum when case sensitivity in authenticateUser doesn't matter
    private String appName;
    private String appUsername;
    private String appAlias;
    private String appUrl;

    public static UserAccountDTO[] convertFromCollection(final Collection<UserAccount> accountCollection, AppHelper appHelper) {
        UserAccountDTO[] accounts = new UserAccountDTO[accountCollection.size()];
        int i = 0;
        for (UserAccount userAccount : accountCollection) {
            accounts[i++] = new UserAccountDTO(userAccount, appHelper);
        }
        return accounts;
    }

    public UserAccountDTO() {
        // For de-serialization
    }

    public UserAccountDTO(UserAccount userAccount, AppHelper appHelper) {
        this.appType = ObjectUtils.toString(userAccount.getAppType());
        this.appName = userAccount.getAppName();
        this.appUsername = userAccount.getAppUsername();
        this.appAlias = StringUtils.defaultIfEmpty(userAccount.getAlias(), userAccount.getAppName());
        this.appUrl = appHelper.buildAppUrl(userAccount.getAppType(), userAccount.getAppName());
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
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
}
