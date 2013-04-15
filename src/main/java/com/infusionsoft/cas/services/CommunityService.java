package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.CommunityAccountDetails;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.AccountException;
import com.infusionsoft.cas.exceptions.UsernameTakenException;
import org.springframework.web.client.RestClientException;

public interface CommunityService {
    String buildUrl();

    String authenticateUser(String appUsername, String appPassword);

    UserAccount registerCommunityUserAccount(User user, CommunityAccountDetails details, String ticketGrantingTicket) throws RestClientException, UsernameTakenException, AccountException;
}
