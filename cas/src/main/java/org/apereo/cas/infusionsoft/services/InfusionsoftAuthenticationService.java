package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.authentication.LoginResult;
import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.domain.User;
import org.jasig.cas.ticket.TicketGrantingTicket;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

public interface InfusionsoftAuthenticationService {
    static final int ALLOWED_LOGIN_ATTEMPTS = 5; // how many tries allowed before locked

    String guessAppName(String url) throws MalformedURLException;

    String guessAppName(URL url);

    AppType guessAppType(String url) throws MalformedURLException;

    AppType guessAppType(URL url);

    LoginResult attemptLoginWithMD5Password(String username, String md5password);

    LoginResult attemptLogin(String username, String password);

    boolean isAccountLocked(String username);

    User getCurrentUser(HttpServletRequest request);

    TicketGrantingTicket getTicketGrantingTicket(HttpServletRequest request);

    void unlockUser(String username);

    void completePasswordReset(User user);

}
