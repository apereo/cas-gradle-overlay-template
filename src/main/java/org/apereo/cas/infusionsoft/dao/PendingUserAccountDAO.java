package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.domain.PendingUserAccount;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PendingUserAccountDAO extends PagingAndSortingRepository<PendingUserAccount, Long> {
    PendingUserAccount findByAppTypeAndAppNameAndAppUsername(AppType appType, String appName, String appUsername);

    PendingUserAccount findByRegistrationCode(String registrationCode);
}
