package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.Authority;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.apereo.cas.infusionsoft.domain.UserIdentity;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> createAttributeMapForUser(@NotNull User user);
    List<UserAccount> findActiveUserAccounts(User user);
    Authority findAuthorityByName(String authorityName);
    User findUserByExternalId(String externalId);
    boolean isDuplicateUsername(User user);
    User loadUser(String username);
    User saveUser(User user) throws InfusionsoftValidationException;
    UserIdentity saveUserIdentity(UserIdentity userIdentity) throws InfusionsoftValidationException;
}
