package org.apereo.cas.infusionsoft.authentication;

import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

/**
 * Special credentials, for internal use only, that re-authenticate a user without checking the password.
 * That's so we can reload the user session after a registration or something without prompting for a new password.
 */
public class LetMeInCredentials extends UsernamePasswordCredentials {
}
