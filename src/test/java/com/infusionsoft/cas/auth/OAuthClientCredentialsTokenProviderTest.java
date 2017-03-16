package com.infusionsoft.cas.auth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OAuthClientCredentialsTokenProviderTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @InjectMocks
    private OAuthClientCredentialsTokenProvider oAuthClientCredentialsTokenProvider;

    @Before
    public void beforeMethod() {
        oAuthClientCredentialsTokenProvider = new OAuthClientCredentialsTokenProvider();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidToken() {
        OAuthAuthenticationToken authToken = oAuthClientCredentialsTokenProvider.createAuthenticationToken(req, resp, "full", "application", "client_credentials", null, "clientId", "clientSecret");
        Assert.assertEquals("clientId", authToken.getClientId());
        Assert.assertEquals("full", authToken.getScope());
        Assert.assertEquals("application", authToken.getApplication());
        Assert.assertEquals("client_credentials", authToken.getGrantType());
        Assert.assertNull(authToken.getPrincipal());
        Assert.assertEquals("clientSecret", authToken.getClientSecret());
    }

    @Test
    public void testInValidGrantType() {
        OAuthAuthenticationToken authToken = oAuthClientCredentialsTokenProvider.createAuthenticationToken(req, resp, "full", "application", "invalid_grant_type", null, "clientId", "clientSecret");
        Assert.assertTrue(authToken == null);
    }
}
