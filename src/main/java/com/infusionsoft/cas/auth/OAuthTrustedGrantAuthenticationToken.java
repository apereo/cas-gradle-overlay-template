package com.infusionsoft.cas.auth;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * An Authentication Token that is specific to the Extended Grant
 */
public class OAuthTrustedGrantAuthenticationToken extends OAuthAuthenticationToken {

    private final Long globalUserId;

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>OAuthTrustedGrantAuthenticationToken</code>, as the {@link
     * #isAuthenticated()} will return <code>false</code>.
     */
    public OAuthTrustedGrantAuthenticationToken(Object principal, Object credentials, String clientId, String clientSecret, String scope, String grantType, String application, Long globalUserId) {
        super(principal, credentials, clientId, clientSecret, scope, grantType, application, null);
        this.globalUserId = globalUserId;
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or <code>AuthenticationProvider</code>
     * implementations that are satisfied with producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     */
    public OAuthTrustedGrantAuthenticationToken(Object principal, Object credentials, String clientId, String clientSecret, String scope, String grantType, String application, Long globalUserId, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, clientId, clientSecret, scope, grantType, application, null, authorities);
        this.globalUserId = globalUserId;
    }

    public Long getGlobalUserId() {
        return globalUserId;
    }
}
