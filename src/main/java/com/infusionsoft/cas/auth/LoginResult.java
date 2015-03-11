package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.LoginAttemptStatus;
import com.infusionsoft.cas.domain.User;

public class LoginResult {

    private final User user;
    private final LoginAttemptStatus loginStatus;
    private int failedAttempts;

    // NOTE: the constructor is made private to enforce constraints on the possible combinations of input parameters
    // For example, disallowing UnlockedByAdmin and making sure user == null for NoSuchUser
    private LoginResult(User user, LoginAttemptStatus loginStatus) {
        this.user = user;
        this.loginStatus = loginStatus;
        this.failedAttempts = 0;
    }

    public User getUser() {
        return user;
    }

    public LoginAttemptStatus getLoginStatus() {
        return loginStatus;
    }

    public static LoginResult AccountLocked(User user) {
        return new LoginResult(user, LoginAttemptStatus.AccountLocked);
    }

    public static LoginResult BadPassword(User user) {
        return new LoginResult(user, LoginAttemptStatus.BadPassword);
    }

    public static LoginResult DisabledUser(User user) {
        return new LoginResult(user, LoginAttemptStatus.DisabledUser);
    }

    public static LoginResult NoSuchUser() {
        return new LoginResult(null, LoginAttemptStatus.NoSuchUser);
    }

    public static LoginResult PasswordExpired(User user) {
        return new LoginResult(user, LoginAttemptStatus.PasswordExpired);
    }

    public static LoginResult Success(User user) {
        return new LoginResult(user, LoginAttemptStatus.Success);
    }

    public static LoginResult OldPassword(User user) {
        return new LoginResult(user, LoginAttemptStatus.OldPassword);
    }

    // Intentionally omitted since it should never be done: public static LoginResult UnlockedByAdmin(User user)

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }
}