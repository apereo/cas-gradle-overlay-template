package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import com.infusionsoft.cas.oauth.exceptions.OAuthUnauthorizedClientException;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class OAuthResourceOwnerAuthenticationProviderTest {

    @InjectMocks
    private OAuthResourceOwnerAuthenticationProvider providerToTest = new OAuthResourceOwnerAuthenticationProvider();

    @Mock
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Mock
    private OAuthService oAuthService;

    private static final String scope = "scope";
    private static final String application = "application";
    private static final String grantType = "password";
    private static final String clientId = "clientId";
    private static final String clientSecret = "clientSecret";
    private static final String serviceKey = "serviceKey";
    private static final String username = "username";
    private static final String password = "password";

    private final OAuthServiceConfig oAuthServiceConfig = new OAuthServiceConfig();
    private static final User user = new User();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        oAuthServiceConfig.setServiceKey(serviceKey);
    }

    @Test
    public void testAuthenticateSuccess() throws Exception {
        OAuthResourceOwnerAuthenticationToken token = new OAuthResourceOwnerAuthenticationToken(username, password, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application);
        doReturn(true).when(oAuthService).isClientAuthorizedForResourceOwnerGrantType(clientId);
        doReturn(LoginResult.Success(user)).when(infusionsoftAuthenticationService).attemptLogin(username, password);

        Authentication actualReturn = providerToTest.authenticate(token);

        Assert.assertTrue(actualReturn instanceof OAuthResourceOwnerAuthenticationToken);
        OAuthResourceOwnerAuthenticationToken actualToken = (OAuthResourceOwnerAuthenticationToken) actualReturn;

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

    @Test(expected = OAuthUnauthorizedClientException.class)
    public void testAuthenticateFailClientNotAuthorized() throws Exception {
        OAuthResourceOwnerAuthenticationToken token = new OAuthResourceOwnerAuthenticationToken(username, password, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application);
        doReturn(false).when(oAuthService).isClientAuthorizedForResourceOwnerGrantType(clientId);

        providerToTest.authenticate(token);
    }

    @Test
    public void testAuthenticateFailFailedLogin() throws Exception {
        OAuthResourceOwnerAuthenticationToken token = new OAuthResourceOwnerAuthenticationToken(username, password, oAuthServiceConfig, clientId, clientSecret, scope, grantType, application);
        doReturn(true).when(oAuthService).isClientAuthorizedForResourceOwnerGrantType(clientId);
        doReturn(LoginResult.BadPassword(user)).when(infusionsoftAuthenticationService).attemptLogin(username, password);

        Authentication actualReturn = providerToTest.authenticate(token);

        Assert.assertNull(actualReturn);
    }

    @Test(expected = NullPointerException.class)
    public void testAuthenticateFailNullToken() throws Exception {
        providerToTest.authenticate(null);
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testAuthenticateFailBadService() throws Exception {
        OAuthResourceOwnerAuthenticationToken token = new OAuthResourceOwnerAuthenticationToken(username, password, null, clientId, clientSecret, scope, grantType, application);
        doReturn(false).when(oAuthService).isClientAuthorizedForResourceOwnerGrantType(clientId);

        providerToTest.authenticate(token);
    }

    @Test
    public void testSupportsSuccess() throws Exception {
        Assert.assertTrue(providerToTest.supports(OAuthResourceOwnerAuthenticationToken.class));
    }

    @Test
    public void testSupportsSuccessSubclass() throws Exception {
        // Mocking generates a subclass
        OAuthResourceOwnerAuthenticationToken mockToken = mock(OAuthResourceOwnerAuthenticationToken.class);
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
