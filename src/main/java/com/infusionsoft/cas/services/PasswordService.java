package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserPassword;

public interface PasswordService {
    boolean isPasswordValid(String username, String password);

    boolean isPasswordExpired(User user);

    boolean isPasswordExpired(UserPassword password);

    int getNumberOfDaysToPasswordExpirationDate(String userId);

    UserPassword getPasswordForUser(User user);

    void setPasswordForUser(User user);

    String validatePassword(User user);
}
