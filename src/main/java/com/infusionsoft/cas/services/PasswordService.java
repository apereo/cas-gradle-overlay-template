package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserPassword;
import com.infusionsoft.cas.exceptions.InfusionsoftValidationException;

public interface PasswordService {
    boolean isPasswordCorrect(User user, String password);

    boolean isPasswordExpired(UserPassword password);

    UserPassword getActivePasswordForUser(User user);

    UserPassword getMatchingPasswordForUser(User user, String password);

    UserPassword getMatchingMD5PasswordForUser(User user, String passwordEncodedMD5);

    void setPasswordForUser(User user, String plainTextPassword) throws InfusionsoftValidationException;

    String validatePassword(User user, String plainTextPassword);

    boolean lastFourPasswordsContains(User user, String password);
}
