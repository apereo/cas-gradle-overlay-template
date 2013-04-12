package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;

import java.util.List;

public interface UserAccountDAO extends JpaDAO<UserAccount> {
    List<UserAccount> findByUserAndDisabled(User user, Boolean disabled);

    UserAccount findByUserAndId(User user, Long id);

    UserAccount findByUserAndAppNameAndAppTypeAndAppUsername(User user, String appName, String appType, String appUsername);

    List<UserAccount> findByUserAndAppTypeAndDisabled(User user, String appType, Boolean disabled);

    List<UserAccount> findByAppNameAndAppTypeAndDisabled(String appName, String appType, Boolean disabled);

    List<UserAccount> findByAppNameAndAppTypeAndAppUsernameAndDisabled(String appName, String appType, String appUsername, Boolean disabled);
}
