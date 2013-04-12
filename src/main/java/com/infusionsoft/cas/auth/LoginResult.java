package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.User;

public class LoginResult {

    public enum LoginStatus {
        AccountLocked,
        BadPassword,
        DisabledUser,
        NoSuchUser,
        PasswordExpired,
        Success
    }

    private final User user;
    private final LoginStatus loginStatus;

    private LoginResult(User user, LoginStatus loginStatus) {
        this.user = user;
        this.loginStatus = loginStatus;
    }

    public User getUser() {
        return user;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public static LoginResult AccountLocked(User user) {
        return new LoginResult(user, LoginStatus.AccountLocked);
    }

    public static LoginResult BadPassword(User user) {
        return new LoginResult(user, LoginStatus.BadPassword);
    }

    public static LoginResult DisabledUser(User user) {
        return new LoginResult(user, LoginStatus.DisabledUser);
    }

    public static LoginResult NoSuchUser() {
        return new LoginResult(null, LoginStatus.NoSuchUser);
    }

    public static LoginResult PasswordExpired(User user) {
        return new LoginResult(user, LoginStatus.PasswordExpired);
    }

    public static LoginResult Success(User user) {
        return new LoginResult(user, LoginStatus.Success);
    }

}