package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.LoginAttempt;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.exceptions.AppCredentialsExpiredException;
import com.infusionsoft.cas.exceptions.AppCredentialsInvalidException;
import org.jasig.cas.ticket.TicketException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public interface InfusionsoftAuthenticationService {
    String guessAppName(String url) throws MalformedURLException;

    String guessAppName(URL url);

    String guessAppType(String url) throws MalformedURLException;

    String guessAppType(URL url);

    void recordLoginAttempt(String username, boolean success);

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
}
