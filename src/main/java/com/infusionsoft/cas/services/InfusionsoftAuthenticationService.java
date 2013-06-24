package com.infusionsoft.cas.services;

import com.infusionsoft.cas.auth.LoginResult;
import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.exceptions.AppCredentialsExpiredException;
import com.infusionsoft.cas.exceptions.AppCredentialsInvalidException;

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

    void verifyAppCredentials(AppType appType, String appName, String appUsername, String appPassword) throws AppCredentialsInvalidException, AppCredentialsExpiredException;

    User getCurrentUser(HttpServletRequest request);

    boolean hasCommunityAccount(User user);

    void unlockUser(String username);

    String getSupportPhoneNumber();
}
