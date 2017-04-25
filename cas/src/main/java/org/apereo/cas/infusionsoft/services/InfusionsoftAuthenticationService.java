package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.authentication.LoginResult;
import org.apereo.cas.infusionsoft.domain.AppType;

import java.net.MalformedURLException;
import java.net.URL;

public interface InfusionsoftAuthenticationService {
    int ALLOWED_LOGIN_ATTEMPTS = 5; // how many tries allowed before locked

    String guessAppName(String url) throws MalformedURLException;

    String guessAppName(URL url);

    AppType guessAppType(String url) throws MalformedURLException;

    AppType guessAppType(URL url);

    LoginResult attemptLogin(String username, String password);

}
