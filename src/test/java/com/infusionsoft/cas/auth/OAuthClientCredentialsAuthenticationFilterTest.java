package com.infusionsoft.cas.auth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OAuthClientCredentialsAuthenticationFilterTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @InjectMocks
    private OAuthClientCredentialsAuthenticationFilter oAuthClientCredentialsAuthenticationFilter;

    @Before
    public void beforeMethod() {
        oAuthClientCredentialsAuthenticationFilter = new OAuthClientCredentialsAuthenticationFilter();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidToken() {
        OAuthAuthenticationToken authToken = oAuthClientCredentialsAuthenticationFilter.createAuthenticationToken(req, resp, "full", "application", "client_credentials", null, "clientId", "clientSecret");
        Assert.assertTrue("clientId".equals(authToken.getClientId()));
        Assert.assertTrue("full".equals(authToken.getScope()));
        Assert.assertTrue("application".equals(authToken.getApplication()));
        Assert.assertTrue("client_credentials".equals(authToken.getGrantType()));
        Assert.assertTrue("service:clientId".equals(authToken.getPrincipal()));
        Assert.assertTrue("clientSecret".equals(authToken.getClientSecret()));
    }

    @Test
    public void testInValidGrantType() {
        OAuthAuthenticationToken authToken = oAuthClientCredentialsAuthenticationFilter.createAuthenticationToken(req, resp, "full", "application", "invalid_grant_type", null, "clientId", "clientSecret");
        Assert.assertTrue(authToken == null);
    }
}
