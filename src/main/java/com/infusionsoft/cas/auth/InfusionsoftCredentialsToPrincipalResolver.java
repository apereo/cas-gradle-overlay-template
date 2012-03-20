package com.infusionsoft.cas.auth;

import org.jasig.cas.authentication.principal.AbstractPersonDirectoryCredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentialsToPrincipalResolver;

/**
 * Alternate version of UsernamePasswordCredentialsToPrincipalResolver, wherein we add our custom attributes.
 */
public class InfusionsoftCredentialsToPrincipalResolver extends AbstractPersonDirectoryCredentialsToPrincipalResolver {
    protected String extractPrincipalId(final Credentials credentials) {
        final UsernamePasswordCredentials usernamePasswordCredentials = (UsernamePasswordCredentials) credentials;
        return usernamePasswordCredentials.getUsername();
    }

    public boolean supports(final Credentials credentials) {
        return credentials != null && UsernamePasswordCredentials.class.isAssignableFrom(credentials.getClass());
    }
}
