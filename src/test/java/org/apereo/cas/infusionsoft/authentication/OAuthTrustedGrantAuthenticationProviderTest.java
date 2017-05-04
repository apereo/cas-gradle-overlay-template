package org.apereo.cas.infusionsoft.authentication;

import org.apereo.cas.infusionsoft.domain.OAuthServiceConfig;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.oauth.exceptions.OAuthInvalidRequestException;
import org.apereo.cas.infusionsoft.oauth.exceptions.OAuthUnauthorizedClientException;
import org.apereo.cas.infusionsoft.oauth.services.OAuthService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class OAuthTrustedGrantAuthenticationProviderTest {

    @InjectMocks
    private OAuthTrustedGrantAuthenticationProvider providerToTest = new OAuthTrustedGrantAuthenticationProvider();

    @Mock
    private OAuthService oAuthService;

    @Mock
    private UserService userService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String scope = "scope";
    private static final String application = "application";
    private static final String grantType = "client_credentials";
    private static final UUID applicationUuid = UUID.randomUUID();
    private static final String clientId = "clientId";
    private static final String clientSecret = "clientSecret";
    private static final String serviceKey = "serviceKey";
    private static final Long globalUserId = 12345L;

    private final OAuthServiceConfig oAuthServiceConfig = new OAuthServiceConfig();
    private static final User user = new User();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        oAuthServiceConfig.setServiceKey(serviceKey);
    }

    @Test
    public void testAuthenticateSuccess() throws Exception {
        OAuthTrustedGrantAuthenticationToken token = new OAuthTrustedGrantAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, globalUserId);
        doReturn(true).when(oAuthService).isClientAuthorizedForTrustedGrantType(clientId);
        doReturn(user).when(userService).loadUser(globalUserId);

        Authentication actualReturn = providerToTest.authenticate(token);

        Assert.assertTrue(actualReturn instanceof OAuthTrustedGrantAuthenticationToken);
        OAuthTrustedGrantAuthenticationToken actualToken = (OAuthTrustedGrantAuthenticationToken) actualReturn;

        Assert.assertSame(user, actualToken.getPrincipal());
        Assert.assertNull(actualToken.getCredentials());
        Assert.assertSame(oAuthServiceConfig, actualToken.getServiceConfig());
        Assert.assertEquals(clientId, actualToken.getClientId());
        Assert.assertEquals(clientSecret, actualToken.getClientSecret());
        Assert.assertEquals(scope, actualToken.getScope());
        Assert.assertEquals(grantType, actualToken.getGrantType());
        Assert.assertEquals(application, actualToken.getApplication());
        Assert.assertNull(actualToken.getTrackingUUID());
        Assert.assertTrue(actualToken.isAuthenticated());
    }

    @Test
    public void testAuthenticateFailClientNotAuthorized() throws Exception {
        OAuthTrustedGrantAuthenticationToken token = new OAuthTrustedGrantAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, globalUserId);
        doReturn(false).when(oAuthService).isClientAuthorizedForTrustedGrantType(clientId);

        thrown.expect(OAuthUnauthorizedClientException.class);
        thrown.expectMessage("oauth.exception.client.not.trusted");

        providerToTest.authenticate(token);
    }

    @Test
    public void testAuthenticateFailNullToken() throws Exception {
        thrown.expect(NullPointerException.class);

        providerToTest.authenticate(null);
    }

    @Test
    public void testAuthenticateFailNoUser() throws Exception {
        OAuthTrustedGrantAuthenticationToken token = new OAuthTrustedGrantAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, globalUserId);
        doReturn(true).when(oAuthService).isClientAuthorizedForTrustedGrantType(clientId);

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.user.not.found");

        providerToTest.authenticate(token);
    }

    @Test
    public void testSupportsSuccess() throws Exception {
        Assert.assertTrue(providerToTest.supports(OAuthTrustedGrantAuthenticationToken.class));
    }

    @Test
    public void testSupportsSuccessSubclass() throws Exception {
        // Mocking generates a subclass
        OAuthTrustedGrantAuthenticationToken mockToken = mock(OAuthTrustedGrantAuthenticationToken.class);
        Assert.assertTrue(providerToTest.supports(mockToken.getClass()));
    }

    @Test
    public void testSupportsFailSuperclass() throws Exception {
        Assert.assertFalse(providerToTest.supports(OAuthAuthenticationToken.class));
    }

    @Test
    public void testSupportsFailObject() throws Exception {
        Assert.assertFalse(providerToTest.supports(Object.class));
    }

    @Test
    public void testSupportsFailNull() throws Exception {
        thrown.expect(NullPointerException.class);

        providerToTest.supports(null);
    }

}
