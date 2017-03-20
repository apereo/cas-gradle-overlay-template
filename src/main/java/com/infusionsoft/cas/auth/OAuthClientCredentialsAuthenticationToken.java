package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * An Authentication Token that is used for the Client Credentials Grant Type
 */
public class OAuthClientCredentialsAuthenticationToken extends OAuthAuthenticationToken {

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>OAuthClientCredentialsAuthenticationToken</code>, as the {@link
     * #isAuthenticated()} will return <code>false</code>.
     */
    public OAuthClientCredentialsAuthenticationToken(String applicationUuid, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String scope, String grantType, String application) {
        super(applicationUuid == null ? null : "service:" + applicationUuid, null, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, null);
    }

    public OAuthClientCredentialsAuthenticationToken(String applicationUuid, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String scope, String grantType, String application, Collection<? extends GrantedAuthority> authorities) {
        super(applicationUuid == null ? null : "service:" + applicationUuid, null, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, null, authorities);
    }

}
