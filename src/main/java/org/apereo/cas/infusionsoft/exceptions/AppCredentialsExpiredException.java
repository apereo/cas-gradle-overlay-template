package org.apereo.cas.infusionsoft.exceptions;

/**
 * Exception that means a user's credentials are expired on a downstream application.
 */
public class AppCredentialsExpiredException extends Exception {
    public AppCredentialsExpiredException(String s) {
        super(s);
    }

    public AppCredentialsExpiredException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
