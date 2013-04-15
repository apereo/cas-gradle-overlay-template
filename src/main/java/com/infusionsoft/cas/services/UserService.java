package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.PendingUserAccount;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.AccountException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User addUser(User user) throws InfusionsoftValidationException;

    User loadUser(String username);

    User loadUser(Long id);

    User findUserByRecoveryCode(String recoveryCode);

    String createPasswordRecoveryCode(User user);

    UserAccount findUserAccount(User user, Long accountId);

    UserAccount findUserAccount(User user, String appName, String appType, String appUsername);

    List<UserAccount> findSortedUserAccounts(User user);

    PendingUserAccount createPendingUserAccount(String appType, String appName, String appUsername, String firstName, String lastName, String email, boolean passwordVerificationRequired);

    UserAccount associateAccountToUser(User user, String appType, String appName, String appUsername, String ticketGrantingTicket) throws AccountException;

    UserAccount associatePendingAccountToUser(User user, String registrationCode) throws AccountException;

    List<UserAccount> findEnabledUserAccounts(String appName, String appType, String appUsername);

    List<UserAccount> findDisabledUserAccounts(String appName, String appType, String appUsername);

    void disableAccount(UserAccount account);

    void enableUserAccount(UserAccount account);

    PendingUserAccount findPendingUserAccount(String registrationCode);

    User findEnabledUser(String username);

    void cleanupLoginAttempts();

    List<UserAccount> findByUserAndDisabled(User user, boolean disabled);

    void updateUser(User user);

    void updateUserAccount(UserAccount userAccount);

    Page<User> findByUsernameLike(String usernameWildcard, Pageable pageable);

    boolean isDuplicateUsername(String username, Long id);

    String resetPassword(User user);

    void changeAssociatedAppUsername(String username, String appName, String appType, String oldAppUsername, String newAppUsername);

}
