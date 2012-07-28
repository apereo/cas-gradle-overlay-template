package com.infusionsoft.cas.exceptions;

/**
 * Cute lil exception that indicates a username was already taken.
 */
public class UsernameTakenException extends Exception {
    public UsernameTakenException(String message) {
        super(message);
    }
}
