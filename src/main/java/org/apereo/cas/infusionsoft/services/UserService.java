package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.domain.Authority;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;

import java.util.List;

public interface UserService {
    User saveUser(User user) throws InfusionsoftValidationException;

    User createUser(User user, String plainTextPassword) throws InfusionsoftValidationException;

    @Deprecated
    User loadUser(String username);

    User loadUser(Long id);

    User findUserByRecoveryCode(String recoveryCode);

    User updatePasswordRecoveryCode(long userId);

    User clearPasswordRecoveryCode(long userId);

    @Deprecated
    UserAccount findUserAccount(User user, String appName, AppType appType);

    @Deprecated
    List<UserAccount> findSortedUserAccountsByAppType(User user, AppType appType);

    @Deprecated
    User findEnabledUser(String username);

    void cleanupLoginAttempts();

    @Deprecated
    List<UserAccount> findActiveUserAccounts(User user);

    @Deprecated
    boolean isDuplicateUsername(User user);

    String resetPassword(User user);

    @Deprecated
    Authority findAuthorityByName(String authorityName);
}
