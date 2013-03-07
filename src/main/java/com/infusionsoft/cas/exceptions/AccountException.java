package com.infusionsoft.cas.exceptions;

/**
 * Indicates something went wrong with the mapping of CAS accounts to app user accounts.
 */
public class AccountException extends Exception {
    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
