package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserAccountDAO extends PagingAndSortingRepository<UserAccount, Long> {
    List<UserAccount> findByUserAndDisabled(User user, Boolean disabled);

    UserAccount findByUserAndId(User user, Long id);

    UserAccount findByUserAndAppNameAndAppType(User user, String appName, AppType appType);

    List<UserAccount> findByAppNameAndAppTypeAndAppUsernameAndUserNot(String appName, AppType appType, String appUsername, User user);

    List<UserAccount> findByUserAndAppTypeAndDisabledOrderByAppNameAsc(User user, AppType appType, Boolean disabled);

    List<UserAccount> findByAppNameAndAppType(String appName, AppType appType);

    List<UserAccount> findByAppNameAndAppTypeAndDisabled(String appName, AppType appType, Boolean disabled);

    List<UserAccount> findByAppNameAndAppTypeAndAppUsername(String appName, AppType appType, String appUsername);

    List<UserAccount> findByAppNameAndAppTypeAndAppUsernameAndDisabled(String appName, AppType appType, String appUsername, Boolean disabled);

    List<UserAccount> findByAppNameAndAppTypeAndUserId(String appName, AppType appType, long userId);

    UserAccount findByAppNameAndAppTypeAndUser_Username(String appName, AppType appType, String username);

    List<UserAccount> findByAppNameAndAppTypeAndUserIdAndDisabled(String appName, AppType appType, long userId, boolean disabled);

    Page<UserAccount> findByUser_UsernameLikeOrAppNameLikeAndAppType(String username, String appName, AppType appType, Pageable pageable);
}
