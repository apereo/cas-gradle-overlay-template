package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.*;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

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

    Map<String, Object> createAttributeMapForUser(@NotNull User user);

    User findUserByExternalId(String externalId);

    UserIdentity findUserIdentityByExternalId(String externalId);

    UserIdentity saveUserIdentity(UserIdentity userIdentity) throws InfusionsoftValidationException;

}
