package org.apereo.cas.infusionsoft.exceptions;

/**
 * Cute lil exception that indicates a username was already taken.
 */
public class CommunityUsernameTakenException extends Exception {
    public CommunityUsernameTakenException(String message) {
        super(message);
    }
}
