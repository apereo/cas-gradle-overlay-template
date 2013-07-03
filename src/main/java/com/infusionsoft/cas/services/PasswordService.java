package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserPassword;
import com.infusionsoft.cas.exceptions.InfusionsoftValidationException;

public interface PasswordService {
    boolean isPasswordCorrect(String username, String password);

    boolean passwordsMatch(UserPassword userPassword, String password);

    boolean md5PasswordsMatch(UserPassword userPassword, String passwordEncodedMD5);

    boolean isPasswordExpired(UserPassword password);

    int getNumberOfDaysToPasswordExpirationDate(String userId);

    UserPassword getPasswordForUser(User user);

    void setPasswordForUser(User user, String plainTextPassword) throws InfusionsoftValidationException;

    String validatePassword(User user, String plainTextPassword);
}
