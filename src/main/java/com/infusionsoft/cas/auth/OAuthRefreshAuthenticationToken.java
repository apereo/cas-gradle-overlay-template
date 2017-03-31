package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * An Authentication Token that is used for the Resource Owner Grant Type
 */
public class OAuthRefreshAuthenticationToken extends OAuthAuthenticationToken {

    private String refreshToken;

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>OAuthRefreshAuthenticationToken</code>, as the {@link
     * #isAuthenticated()} will return <code>false</code>.
     *
     * @param oAuthServiceConfig oAuthServiceConfig
     * @param clientId           clientId
     * @param clientSecret       clientSecret
     * @param grantType          grantType
     * @param refreshToken       refreshToken
     */
    public OAuthRefreshAuthenticationToken(OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String grantType, String refreshToken) {
        super(null, null, oAuthServiceConfig, clientId, clientSecret, null, grantType, null, null);
        this.refreshToken = refreshToken;
    }

    public OAuthRefreshAuthenticationToken(OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String grantType, String refreshToken, Collection<? extends GrantedAuthority> authorities) {
        super(null, null, oAuthServiceConfig, clientId, clientSecret, null, grantType, null, null, authorities);
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
