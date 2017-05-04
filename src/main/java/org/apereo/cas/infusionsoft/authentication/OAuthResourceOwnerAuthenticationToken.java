package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.domain.OAuthServiceConfig;
import org.apereo.cas.infusionsoft.domain.User;
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
     * @param username           username
     * @param password           password
     * @param oAuthServiceConfig oAuthServiceConfig
     * @param clientId           clientId
     * @param clientSecret       clientSecret
     * @param scope              scope
     * @param grantType          grantType
     * @param application        application
     */
    public OAuthResourceOwnerAuthenticationToken(String username, String password, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String scope, String grantType, String application) {
        super(username, password, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, null);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or <code>AuthenticationProvider</code>
     * implementations that are satisfied with producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param user               user
     * @param oAuthServiceConfig oAuthServiceConfig
     * @param clientId           clientId
     * @param clientSecret       clientSecret
     * @param scope              scope
     * @param grantType          grantType
     * @param application        application
     * @param authorities        authorities
     */
    public OAuthResourceOwnerAuthenticationToken(User user, OAuthServiceConfig oAuthServiceConfig, String clientId, String clientSecret, String scope, String grantType, String application, Collection<? extends GrantedAuthority> authorities) {
        super(user, null, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, null, authorities);
    }

}
