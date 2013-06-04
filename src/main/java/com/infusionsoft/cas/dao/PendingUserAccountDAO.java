package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.PendingUserAccount;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PendingUserAccountDAO extends PagingAndSortingRepository<PendingUserAccount, Long> {
    PendingUserAccount findByAppTypeAndAppNameAndAppUsername(AppType appType, String appName, String appUsername);

    PendingUserAccount findByRegistrationCode(String registrationCode);
}
