package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.dao.UserAccountDAO;
import org.apereo.cas.infusionsoft.dao.UserDAO;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(transactionManager = "transactionManager")
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;
    private UserAccountDAO userAccountDAO;

    public UserServiceImpl(UserDAO userDAO, UserAccountDAO userAccountDAO) {
        this.userDAO = userDAO;
        this.userAccountDAO = userAccountDAO;
    }

    @Override
    public User loadUser(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public List<UserAccount> findActiveUserAccounts(User user) {
        return userAccountDAO.findByUserAndDisabled(user, false);
    }
}