package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.LoginAttempt;
import com.infusionsoft.cas.auth.LoginResult;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.exceptions.AppCredentialsExpiredException;
import com.infusionsoft.cas.exceptions.AppCredentialsInvalidException;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public interface InfusionsoftAuthenticationService {
    String guessAppName(String url) throws MalformedURLException;

    String guessAppName(URL url);

    String guessAppType(String url) throws MalformedURLException;

    String guessAppType(URL url);

    LoginResult attemptLoginWithMD5Password(String username, String md5password);

    LoginResult attemptLogin(String username, String password);

    List<LoginAttempt> getRecentLoginAttempts(String username);

    int countConsecutiveFailedLogins(String username);

    LoginAttempt getMostRecentFailedLogin(String username);

    boolean isAccountLocked(String username);

    boolean isAppAssociated(User user, URL url);

    void verifyAppCredentials(String appType, String appName, String appUsername, String appPassword) throws AppCredentialsInvalidException, AppCredentialsExpiredException;

    //TODO: Removed from trying to get app up
//    void createTicketGrantingTicket(String username, HttpServletRequest request, HttpServletResponse response) throws TicketException;

//    String getTicketGrantingTicketId(HttpServletRequest request);

//    void destroyTicketGrantingTicket(HttpServletRequest request, HttpServletResponse response);

    User getCurrentUser(HttpServletRequest request);

    boolean isUserAssociated(User user, String appType, String appName);

    boolean hasCommunityAccount(User user);

    void unlockUser(String username);
}
