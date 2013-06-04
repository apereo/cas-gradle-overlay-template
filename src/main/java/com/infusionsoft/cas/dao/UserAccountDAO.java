package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserAccountDAO extends PagingAndSortingRepository<UserAccount, Long> {
    List<UserAccount> findByUserAndDisabled(User user, Boolean disabled);

    UserAccount findByUserAndId(User user, Long id);

    UserAccount findByUserAndAppNameAndAppType(User user, String appName, AppType appType);

    List<UserAccount> findByAppNameAndAppTypeAndAppUsernameAndUserNot(String appName, AppType appType, String appUsername, User user);

    List<UserAccount> findByUserAndAppTypeAndDisabled(User user, AppType appType, Boolean disabled);

    List<UserAccount> findByAppNameAndAppType(String appName, AppType appType);

    List<UserAccount> findByAppNameAndAppTypeAndDisabled(String appName, AppType appType, Boolean disabled);

    List<UserAccount> findByAppNameAndAppTypeAndAppUsername(String appName, AppType appType, String appUsername);

    List<UserAccount> findByAppNameAndAppTypeAndAppUsernameAndDisabled(String appName, AppType appType, String appUsername, Boolean disabled);

    List<UserAccount> findByAppNameAndAppTypeAndUserId(String appName, AppType appType, long userId);

    List<UserAccount> findByAppNameAndAppTypeAndUserIdAndDisabled(String appName, AppType appType, long userId, boolean disabled);
}
