package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;

import java.util.List;

public interface UserService {
    User loadUser(String username);
    List<UserAccount> findActiveUserAccounts(User user);
}
