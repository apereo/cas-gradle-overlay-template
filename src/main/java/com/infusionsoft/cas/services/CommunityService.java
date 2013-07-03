package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.CommunityAccountDetails;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.AccountException;
import com.infusionsoft.cas.exceptions.CommunityUsernameTakenException;
import org.springframework.web.client.RestClientException;

public interface CommunityService {
    String buildUrl();

    String getBaseUrl();

    String authenticateUser(String appUsername, String appPassword);

    UserAccount registerCommunityUserAccount(User user, CommunityAccountDetails details) throws RestClientException, CommunityUsernameTakenException, AccountException;
}
