package com.infusionsoft.cas.auth;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * An Authentication Token that is used for the Resource Owner Grant Type
 */
public class OAuthResourceOwnerAuthenticationToken extends OAuthAuthenticationToken {

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>OAuthResourceOwnerAuthenticationToken</code>, as the {@link
     * #isAuthenticated()} will return <code>false</code>.
     *
     */
    public OAuthResourceOwnerAuthenticationToken(Object principal, Object credentials, String clientId, String clientSecret, String scope, String grantType, String application) {
        super(principal, credentials, clientId, clientSecret, scope, grantType, application);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or <code>AuthenticationProvider</code>
     * implementations that are satisfied with producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     */
    public OAuthResourceOwnerAuthenticationToken(Object principal, Object credentials, String clientId, String clientSecret, String scope, String grantType, String application, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, clientId, clientSecret, scope, grantType, application, authorities);
    }

}
