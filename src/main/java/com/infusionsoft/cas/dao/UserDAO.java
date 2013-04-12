package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.User;

import java.util.List;

public interface UserDAO extends JpaDAO<User> {
    User findByUsername(String username);

    User findByUsernameAndNotId(String username, Long id);

    User findByUsernameAndEnabled(String username, Boolean enabled);

    User findByPasswordRecoveryCode(String passwordRecoveryCode);

    List<User> findByUsernameWildcard(String username);
}
