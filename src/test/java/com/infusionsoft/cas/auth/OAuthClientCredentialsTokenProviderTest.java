package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OAuthClientCredentialsTokenProviderTest {
    private static final String scope = "full";
    private static final String application = "application";
    private static final String grantType = "client_credentials";
    private static final String clientId = "clientId";
    private static final String clientSecret = "clientSecret";

    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;

    private OAuthClientCredentialsTokenProvider tokenProviderToTest = new OAuthClientCredentialsTokenProvider();

    @Before
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidToken() {
        OAuthClientCredentialsAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, null, clientId, clientSecret);
        Assert.assertEquals(clientId, authToken.getClientId());
        Assert.assertEquals(scope, authToken.getScope());
        Assert.assertEquals(application, authToken.getApplication());
        Assert.assertEquals(grantType, authToken.getGrantType());
        Assert.assertNull(authToken.getPrincipal());
        Assert.assertEquals(clientSecret, authToken.getClientSecret());
    }

    @Test
    public void testInvalidGrantType() {
        OAuthAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, "invalid_grant_type", null, clientId, clientSecret);
        Assert.assertTrue(authToken == null);
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testMissingClientId() {
        tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, null, "", clientSecret);
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testMissingClientSecret() {
        tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, null, clientId, "");
    }

}
