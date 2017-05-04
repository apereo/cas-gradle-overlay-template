package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.domain.OAuthServiceConfig;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;
import java.util.Objects;

/**
 * This is a copy of UsernamePasswordAuthenticationToken,
 * because when attempting to authenticate oauth client credentials
 * the default basic auth filter was seeing the username being different
 * and attempting an authentication against the local CAS database for
 * client_id and client_secret
 * <p>
 * The class was mostly copied from UsernamePasswordToken
 */
public class OAuthAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    protected final Object principal;
    protected final OAuthServiceConfig serviceConfig;
    protected final String clientId;
    protected final String clientSecret;
    protected final String scope;
    protected final String grantType;
    protected final String application;
    protected final String trackingUUID;
    protected Object credentials;

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>OAuthAuthenticationToken</code>, as the {@link
     * #isAuthenticated()} will return <code>false</code>.
     *
     * @param principal     principal
     * @param credentials   credentials
     * @param serviceConfig serviceConfig
     * @param clientId      clientId
     * @param clientSecret  clientSecret
     * @param scope         scope
     * @param grantType     grantType
     * @param application   application application
     * @param trackingUUID  trackingUUID
     */
    public OAuthAuthenticationToken(Object principal, Object credentials, OAuthServiceConfig serviceConfig, String clientId, String clientSecret, String scope, String grantType, String application, String trackingUUID) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.serviceConfig = Objects.requireNonNull(serviceConfig, "serviceConfig must not be null");
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.grantType = grantType;
        this.application = application;
        this.trackingUUID = trackingUUID;
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or <code>AuthenticationProvider</code>
     * implementations that are satisfied with producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     * <p>
     *
     * @param principal     principal
     * @param credentials   credentials
     * @param serviceConfig serviceConfig
     * @param clientId      clientId
     * @param clientSecret  clientSecret
     * @param scope         scope
     * @param grantType     grantType
     * @param application   application
     * @param trackingUUID  trackingUUID
     * @param authorities   authorities
     */
    public OAuthAuthenticationToken(Object principal, Object credentials, OAuthServiceConfig serviceConfig, String clientId, String clientSecret, String scope, String grantType, String application, String trackingUUID, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.serviceConfig = Objects.requireNonNull(serviceConfig, "serviceConfig must not be null");
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.grantType = grantType;
        this.application = application;
        this.trackingUUID = trackingUUID;
        super.setAuthenticated(true); // must use super, as we override
    }

    public Object getCredentials() {
        return this.credentials;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public OAuthServiceConfig getServiceConfig() {
        return serviceConfig;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getScope() {
        return scope;
    }

    public String getApplication() {
        return application;
    }

    public String getTrackingUUID() {
        return trackingUUID;
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
