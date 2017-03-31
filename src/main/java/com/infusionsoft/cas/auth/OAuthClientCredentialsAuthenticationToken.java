package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

/**
 * An Authentication Token that is used for the Client Credentials Grant Type
 */
public class OAuthClientCredentialsAuthenticationToken extends OAuthAuthenticationToken {

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>OAuthClientCredentialsAuthenticationToken</code>, as the {@link
     * #isAuthenticated()} will return <code>false</code>.
     */
    public OAuthClientCredentialsAuthenticationToken(OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String scope, String grantType, String application) {
        super(null, null, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, null);
    }

    public OAuthClientCredentialsAuthenticationToken(String applicationUuid, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String scope, String grantType, String application, Collection<? extends GrantedAuthority> authorities) {
        super("service:" + Objects.requireNonNull(applicationUuid, "applicationUuid must not be null"), null, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, null, authorities);
    }

}
