package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserPassword;

import java.util.List;

public interface UserPasswordDAO extends JpaDAO<UserPassword> {
    UserPassword findByUsernameAndPassword(String username, String encodedPassword);

    UserPassword findByUsernameAndMD5Password(String username, String passwordEncodedMD5);

    UserPassword findByUsername(String username);

    List<UserPassword> findLastFourByUsername(User user);
}
