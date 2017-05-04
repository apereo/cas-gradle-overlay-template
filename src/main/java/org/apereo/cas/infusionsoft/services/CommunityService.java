package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.CommunityAccountDetails;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.apereo.cas.infusionsoft.exceptions.AccountException;
import org.apereo.cas.infusionsoft.exceptions.CommunityUsernameTakenException;
import org.springframework.web.client.RestClientException;

public interface CommunityService {
    String buildUrl();

    String getBaseUrl();

    String authenticateUser(String appUsername, String appPassword);

    UserAccount registerCommunityUserAccount(User user, CommunityAccountDetails details) throws RestClientException, CommunityUsernameTakenException, AccountException;
}
