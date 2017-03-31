package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.mock;

public class OAuthRefreshAuthenticationProviderTest {

    @InjectMocks
    private OAuthRefreshAuthenticationProvider providerToTest = new OAuthRefreshAuthenticationProvider();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String grantType = "refresh";
    private static final String clientId = "clientId";
    private static final String clientSecret = "clientSecret";
    private static final String serviceKey = "serviceKey";
    private static final String refreshToken = "refreshToken";

    private final OAuthServiceConfig oAuthServiceConfig = new OAuthServiceConfig();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        oAuthServiceConfig.setServiceKey(serviceKey);
    }

    @Test
    public void testAuthenticateSuccess() throws Exception {
        OAuthRefreshAuthenticationToken token = new OAuthRefreshAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, grantType, refreshToken);

        Authentication actualReturn = providerToTest.authenticate(token);

        Assert.assertTrue(actualReturn instanceof OAuthRefreshAuthenticationToken);
        OAuthRefreshAuthenticationToken actualToken = (OAuthRefreshAuthenticationToken) actualReturn;

        Assert.assertNull(actualToken.getPrincipal());
        Assert.assertNull(actualToken.getCredentials());
        Assert.assertSame(oAuthServiceConfig, actualToken.getServiceConfig());
        Assert.assertEquals(clientId, actualToken.getClientId());
        Assert.assertEquals(clientSecret, actualToken.getClientSecret());
        Assert.assertNull(actualToken.getScope());
        Assert.assertEquals(grantType, actualToken.getGrantType());
        Assert.assertNull(actualToken.getApplication());
        Assert.assertNull(actualToken.getTrackingUUID());
        Assert.assertEquals(refreshToken, actualToken.getRefreshToken());
        Assert.assertTrue(actualToken.isAuthenticated());
    }

    @Test
    public void testAuthenticateFailNullToken() throws Exception {
        thrown.expect(NullPointerException.class);

        providerToTest.authenticate(null);
    }

    @Test
    public void testAuthenticateFailMissingRefresh() throws Exception {
        OAuthRefreshAuthenticationToken token = new OAuthRefreshAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, grantType, "");

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.refreshToken.missing");

        providerToTest.authenticate(token);
    }

    @Test
    public void testSupportsSuccess() throws Exception {
        Assert.assertTrue(providerToTest.supports(OAuthRefreshAuthenticationToken.class));
    }

    @Test
    public void testSupportsSuccessSubclass() throws Exception {
        // Mocking generates a subclass
        OAuthRefreshAuthenticationToken mockToken = mock(OAuthRefreshAuthenticationToken.class);
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
