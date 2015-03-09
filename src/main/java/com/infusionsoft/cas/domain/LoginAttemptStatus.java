package com.infusionsoft.cas.domain;

/**
* All the possible results of attempted logins, plus the "dummy login" of being unlocked by an admin
*/
public enum LoginAttemptStatus {
    AccountLocked,
    BadPassword,
    DisabledUser,
    NoSuchUser,
    OldPassword,
    PasswordExpired(true),
    PasswordReset(true),
    SecurityQuestionsOptional(true),
    SecurityQuestionsRequired(true),
    Success(true),
    UnlockedByAdmin(true);

    private boolean successful;

    private LoginAttemptStatus() {
        this(false);
    }

    private LoginAttemptStatus(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
