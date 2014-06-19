package com.infusionsoft.cas.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * This is a copy of UsernamePasswordAuthenticationToken,
 * because when attempting to authenticate oauth client credentials
 * the default basic auth filter was seeing the username being different
 * and attempting an authentication against the local CAS database for
 * client_id and client_secret
 *
 * The class was mostly copied from UsernamePasswordToken
 */
public class OAuthClientCredentialAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    //~ Instance fields ================================================================================================

    private final Object principal;
    private Object credentials;

    private final String clientId;
    private final String clientSecret;

    //~ Constructors ===================================================================================================

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>OAuthClientCredentialAuthenticationToken</code>, as the {@link
     * #isAuthenticated()} will return <code>false</code>.
     *
     */
    public OAuthClientCredentialAuthenticationToken(Object principal, Object credentials, String clientId, String clientSecret) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or <code>AuthenticationProvider</code>
     * implementations that are satisfied with producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param principal
     * @param credentials
     * @param clientId
     * @param clientSecret
     * @param authorities
     */
    public OAuthClientCredentialAuthenticationToken(Object principal, Object credentials, String clientId, String clientSecret, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        super.setAuthenticated(true); // must use super, as we override
    }


    //~ Methods ========================================================================================================

    public Object getCredentials() {
        return this.credentials;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return  clientSecret;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }
}
