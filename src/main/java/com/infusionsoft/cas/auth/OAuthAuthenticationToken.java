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
public class OAuthAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    protected final Object principal;
    protected final String clientId;
    protected final String clientSecret;
    protected final String scope;
    protected final String grantType;
    protected final String application;
    protected Object credentials;

    public static final String ANONYMOUS_USER = "anonymousUser";

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>OAuthAuthenticationToken</code>, as the {@link
     * #isAuthenticated()} will return <code>false</code>.
     *
     */
    public OAuthAuthenticationToken(Object principal, Object credentials, String clientId, String clientSecret, String scope, String grantType, String application) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.grantType = grantType;
        this.application = application;
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
    public OAuthAuthenticationToken(Object principal, Object credentials, String clientId, String clientSecret, String scope, String grantType, String application, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.grantType = grantType;
        this.application = application;
        super.setAuthenticated(true); // must use super, as we override
    }

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

    public String getScope() {
        return scope;
    }

    public String getApplication() {
        return application;
    }

    public String getGrantType() {
        return grantType;
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
