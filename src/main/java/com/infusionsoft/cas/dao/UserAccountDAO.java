package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserAccountDAO extends PagingAndSortingRepository<UserAccount, Long> {
    List<UserAccount> findByUserAndDisabled(User user, Boolean disabled);

    UserAccount findByUserAndId(User user, Long id);

    UserAccount findByUserAndAppNameAndAppType(User user, String appName, String appType);

    List<UserAccount> findByUserAndAppTypeAndDisabled(User user, String appType, Boolean disabled);

    List<UserAccount> findByAppNameAndAppTypeAndDisabled(String appName, String appType, Boolean disabled);

    List<UserAccount> findByAppNameAndAppTypeAndAppUsernameAndDisabled(String appName, String appType, String appUsername, Boolean disabled);

    List<UserAccount> findByAppNameAndAppTypeAndUserIdAndDisabled(String appName, String appType, long userId, boolean disabled);
}
