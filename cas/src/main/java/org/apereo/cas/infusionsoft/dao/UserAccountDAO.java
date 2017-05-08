package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@Deprecated
public interface UserAccountDAO extends PagingAndSortingRepository<UserAccount, Long> {

    @Deprecated
    List<UserAccount> findByUserAndDisabled(User user, Boolean disabled);

    @Deprecated
    UserAccount findByUserAndAppNameAndAppType(User user, String appName, AppType appType);

    @Deprecated
    List<UserAccount> findByUserAndAppTypeAndDisabledOrderByAppNameAsc(User user, AppType appType, Boolean disabled);

}
