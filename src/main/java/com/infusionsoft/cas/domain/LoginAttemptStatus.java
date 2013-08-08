package com.infusionsoft.cas.domain;

/**
* All the possible results of attempted logins, plus the "dummy login" of being unlocked by an admin
*/
public enum LoginAttemptStatus {
    AccountLocked,
    BadPassword,
    DisabledUser,
    NoSuchUser,
    PasswordExpired,
    Success,
    OldPassword,
    UnlockedByAdmin
}
