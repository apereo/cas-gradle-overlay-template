package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.domain.Authority;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftAccountException;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Map;

public interface UserService {
    User saveUser(User user) throws InfusionsoftValidationException;

    User createUser(User user, String plainTextPassword) throws InfusionsoftValidationException;

    User loadUser(String username);

    User loadUser(Long id);

    User findUserByRecoveryCode(String recoveryCode);

    User updatePasswordRecoveryCode(long userId);

    User clearPasswordRecoveryCode(long userId);

    UserAccount findUserAccount(User user, Long accountId);

    UserAccount findUserAccount(User user, String appName, AppType appType);

    Map<AppType, List<UserAccount>> findSortedUserAccounts(User user);

    List<UserAccount> findSortedUserAccountsByAppType(User user, AppType appType);

    UserAccount associateAccountToUser(User user, AppType appType, String appName, String appUsername) throws InfusionsoftAccountException;

    List<UserAccount> findEnabledUserAccounts(String appName, AppType appType, String appUsername);

    List<UserAccount> findEnabledUserAccounts(String appName, AppType appType, long globalUserId);

    List<UserAccount> findUserAccounts(String appName, AppType appType, String appUsername);

    List<UserAccount> findUserAccounts(String appName, AppType appType, long globalUserId);

    UserAccount findUserAccountByInfusionsoftId(String appName, AppType appType, String infusionsoftId);

    List<UserAccount> findDisabledUserAccounts(String appName, AppType appType, String appUsername);

    List<UserAccount> findDisabledUserAccounts(String appName, AppType appType, long globalUserId);

    void deleteAccount(UserAccount account);

    void disableAccount(UserAccount account);

    void enableUserAccount(UserAccount account);

    User findEnabledUser(String username);

    void cleanupLoginAttempts();

    List<UserAccount> findActiveUserAccounts(User user);

    UserAccount saveUserAccount(UserAccount userAccount);

    Page<User> findByUsernameLike(String usernameWildcard, Pageable pageable);

    boolean isDuplicateUsername(User user);

    void changeAssociatedAppUsername(User user, String appName, AppType appType, String newAppUsername) throws InfusionsoftAccountException;

    List<Authority> findAllAuthorities();

    Authority findAuthorityByName(String authorityName);

    boolean validateUserApplication(String application) throws AccessDeniedException;

    Page<UserAccount> findUserAccountsByUsernameLikeOrAppNameLikeAndAppType(String username, String appName, AppType appType, Pageable pageable);
}
