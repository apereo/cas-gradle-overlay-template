package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.mock;

public class OAuthRefreshAuthenticationProviderTest {

    @InjectMocks
    private OAuthRefreshAuthenticationProvider providerToTest = new OAuthRefreshAuthenticationProvider();

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
        OAuthRefreshAuthenticationToken token = new OAuthRefreshAuthenticationToken(null, null, oAuthServiceConfig, clientId, clientSecret, grantType, refreshToken);

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

    @Test(expected = NullPointerException.class)
    public void testAuthenticateFailNullToken() throws Exception {
        providerToTest.authenticate(null);
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testAuthenticateFailBadService() throws Exception {
        OAuthRefreshAuthenticationToken token = new OAuthRefreshAuthenticationToken(null, null, null, clientId, clientSecret, grantType, refreshToken);

        providerToTest.authenticate(token);
    }

    @Test
    public void testAuthenticateFailMissingRefresh() throws Exception {
        OAuthRefreshAuthenticationToken token = new OAuthRefreshAuthenticationToken(null, null, oAuthServiceConfig, clientId, clientSecret, grantType, "");

        final Authentication actualReturn = providerToTest.authenticate(token);

        Assert.assertNull(actualReturn);
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

    @Test(expected = NullPointerException.class)
    public void testSupportsFailNull() throws Exception {
        providerToTest.supports(null);
    }

}
