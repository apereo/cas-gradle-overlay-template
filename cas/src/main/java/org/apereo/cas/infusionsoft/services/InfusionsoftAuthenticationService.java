package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.authentication.LoginResult;
import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.ticket.TicketGrantingTicket;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

public interface InfusionsoftAuthenticationService {

    int ALLOWED_LOGIN_ATTEMPTS = 5; // how many tries allowed before locked

    LoginResult attemptLoginWithMD5Password(String username, String md5password);

    LoginResult attemptLogin(String username, String password);

    boolean isAccountLocked(String username);

    void completePasswordReset(User user);

}
