package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.dto.OAuthApplication;
import com.infusionsoft.cas.oauth.exceptions.OAuthUnauthorizedClientException;
import com.infusionsoft.cas.oauth.services.OAuthService;
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

public class OAuthClientCredentialsAuthenticationProviderTest {

    @InjectMocks
    private OAuthClientCredentialsAuthenticationProvider providerToTest = new OAuthClientCredentialsAuthenticationProvider();

    @Mock
    private OAuthService oAuthService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String scope = "scope";
    private static final String application = "application";
    private static final String grantType = "client_credentials";
    private static final UUID applicationUuid = UUID.randomUUID();
    private static final String clientId = "clientId";
    private static final String clientSecret = "clientSecret";
    private static final String serviceKey = "serviceKey";

    private final OAuthServiceConfig oAuthServiceConfig = new OAuthServiceConfig();
    private final OAuthApplication oAuthApplication = new OAuthApplication("id", applicationUuid, "name", "desc", "", "");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        oAuthServiceConfig.setServiceKey(serviceKey);
    }

    @Test
    public void testAuthenticateSuccess() throws Exception {
        OAuthClientCredentialsAuthenticationToken token = new OAuthClientCredentialsAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application);
        doReturn(true).when(oAuthService).isClientAuthorizedForClientCredentialsGrantType(clientId);
        doReturn(oAuthApplication).when(oAuthService).fetchApplication(serviceKey, clientId, null, "code");

        Authentication actualReturn = providerToTest.authenticate(token);

        Assert.assertTrue(actualReturn instanceof OAuthClientCredentialsAuthenticationToken);
        OAuthClientCredentialsAuthenticationToken actualToken = (OAuthClientCredentialsAuthenticationToken) actualReturn;

        Assert.assertEquals("service:" + applicationUuid, actualToken.getPrincipal());
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
    public void testAuthenticateFailNullToken() throws Exception {
        thrown.expect(NullPointerException.class);

        providerToTest.authenticate(null);
    }

    @Test
    public void testAuthenticateFailClientNotAuthorized() throws Exception {
        OAuthClientCredentialsAuthenticationToken token = new OAuthClientCredentialsAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application);
        doReturn(false).when(oAuthService).isClientAuthorizedForClientCredentialsGrantType(clientId);

        thrown.expect(OAuthUnauthorizedClientException.class);
        thrown.expectMessage("oauth.exception.client.not.trusted.service");

        providerToTest.authenticate(token);
    }

    @Test
    public void testSupportsSuccess() throws Exception {
        Assert.assertTrue(providerToTest.supports(OAuthClientCredentialsAuthenticationToken.class));
    }

    @Test
    public void testSupportsSuccessSubclass() throws Exception {
        // Mocking generates a subclass
        OAuthClientCredentialsAuthenticationToken mockToken = mock(OAuthClientCredentialsAuthenticationToken.class);
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
