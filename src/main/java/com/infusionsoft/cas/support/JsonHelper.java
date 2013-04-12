package com.infusionsoft.cas.support;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Locale;

@Component
public class JsonHelper {

    @Autowired
    AppHelper appHelper;

    @Autowired
    MessageSource messageSource;

    /**
     * Builds a JSON string that represents a CAS user and all linked accounts.
     */
    public String buildUserInfoJSON(User user) {
        JSONObject json = new JSONObject();

        json.put("id", user.getId());
        json.put("username", user.getUsername());
        json.put("displayName", user.getFirstName() + " " + user.getLastName());
        json.put("firstName", user.getFirstName());
        json.put("lastName", user.getLastName());
        json.put("accounts", buildUserAccountsJSON(user.getAccounts()));

        // Get rid of the JSON-optional escaped slashes because Ruby's Psych parser chokes on them
        return json.toJSONString().replaceAll("\\\\/", "/");
    }

    public JSONArray buildUserAccountsJSON(Collection<UserAccount> userAccounts) {
        JSONArray accountsArray = new JSONArray();

        for (UserAccount account : userAccounts) {
            if (!account.isDisabled()) {
                JSONObject accountToAdd = new JSONObject();

                accountToAdd.put("type", account.getAppType());
                accountToAdd.put("appName", account.getAppName());
                accountToAdd.put("userName", account.getAppUsername());
                accountToAdd.put("appAlias", account.getAlias());
                accountToAdd.put("appUrl", appHelper.buildAppUrl(account.getAppType(), account.getAppName()));

                accountsArray.add(accountToAdd);
            }
        }
        return accountsArray;
    }

    public String buildErrorJson(String code) {
        JSONObject json = new JSONObject();

        json.put("code", code);
        json.put("message", messageSource.getMessage(code, new Object[]{}, Locale.ENGLISH));

        return json.toJSONString();
    }
}
