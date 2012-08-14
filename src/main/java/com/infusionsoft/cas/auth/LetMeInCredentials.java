package com.infusionsoft.cas.auth;

/**
 * Special credentials, for internal use only, that re-authenticate a user without checking the password.
 * That's so we can reload the user session after a registration or something without prompting for a new password.
 */
public class LetMeInCredentials extends InfusionsoftCredentials {
}
