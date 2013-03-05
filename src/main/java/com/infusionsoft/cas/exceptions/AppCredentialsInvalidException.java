package com.infusionsoft.cas.exceptions;

/**
 * Exception that means a user's credentials are invalid on a downstream application.
 */
public class AppCredentialsInvalidException extends Exception {
    public AppCredentialsInvalidException(String s) {
        super(s);
    }

    public AppCredentialsInvalidException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
