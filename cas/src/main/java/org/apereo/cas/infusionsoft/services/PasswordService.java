package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserPassword;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;

public interface PasswordService {
    boolean isPasswordExpired(UserPassword password);

    UserPassword getLatestPassword(User user);

    UserPassword getMatchingPasswordForUser(User user, String password);

    UserPassword getMatchingMD5PasswordForUser(User user, String passwordEncodedMD5);

    void setPasswordForUser(User user, String plainTextPassword) throws InfusionsoftValidationException;

    String validatePassword(User user, String plainTextPassword);

    boolean lastFourPasswordsContains(User user, String password);
}
