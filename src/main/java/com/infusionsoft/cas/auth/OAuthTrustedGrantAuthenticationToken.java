package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
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
     *
     * @param principal          principal
     * @param credentials        credentials
     * @param oAuthServiceConfig oAuthServiceConfig
     * @param clientId           clientId
     * @param clientSecret       clientSecret
     * @param scope              scope
     * @param grantType          grantType
     * @param application        application
     * @param globalUserId       globalUserId
     */
    public OAuthTrustedGrantAuthenticationToken(Object principal, Object credentials, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String scope, String grantType, String application, Long globalUserId) {
        super(principal, credentials, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, null);
        this.globalUserId = globalUserId;
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or <code>AuthenticationProvider</code>
     * implementations that are satisfied with producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param principal          principal
     * @param credentials        credentials
     * @param oAuthServiceConfig oAuthServiceConfig
     * @param clientId           clientId
     * @param clientSecret       clientSecret
     * @param scope              scope
     * @param grantType          grantType
     * @param application        application
     * @param globalUserId       globalUserId
     * @param authorities        authorities
     */
    public OAuthTrustedGrantAuthenticationToken(Object principal, Object credentials, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String scope, String grantType, String application, Long globalUserId, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, null, authorities);
        this.globalUserId = globalUserId;
    }

    public Long getGlobalUserId() {
        return globalUserId;
    }
}
