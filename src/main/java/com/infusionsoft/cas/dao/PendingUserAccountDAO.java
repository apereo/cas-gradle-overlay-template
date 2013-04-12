package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.PendingUserAccount;

public interface PendingUserAccountDAO extends JpaDAO<PendingUserAccount> {
    PendingUserAccount findByAppTypeAndAppNameAndAppUsername(String appType, String appName, String appUsername);

    PendingUserAccount findByRegistrationCode(String registrationCode);
}
