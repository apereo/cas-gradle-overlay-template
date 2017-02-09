package org.apereo.cas.infusionsoft.exceptions;

/**
 * Indicates something went wrong with the mapping of CAS accounts to app user accounts.
 */
public class InfusionsoftAccountException extends Exception {

    public InfusionsoftAccountException(String message, Throwable cause) {
        super(message, cause);
    }

}
