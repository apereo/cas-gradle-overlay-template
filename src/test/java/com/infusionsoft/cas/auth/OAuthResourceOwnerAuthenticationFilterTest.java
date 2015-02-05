package com.infusionsoft.cas.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OAuthResourceOwnerAuthenticationFilterTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private LogFactory logFactory;
    @Mock
    private Log log;
    private OAuthResourceOwnerAuthenticationFilter oAuthResourceOwnerAuthenticationFilter;

    @Before
    public void beforeMethod() {
        oAuthResourceOwnerAuthenticationFilter = new OAuthResourceOwnerAuthenticationFilter();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidToken() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");
        Mockito.when(req.getParameter("password")).thenReturn("Password123");
        OAuthAuthenticationToken authToken = oAuthResourceOwnerAuthenticationFilter.createAuthenticationToken(req, resp, "full", "application", "password", null, "clientId", "clientSecret");
        Assert.assertTrue("clientId".equals(authToken.getClientId()));
        Assert.assertTrue("full".equals(authToken.getScope()));
        Assert.assertTrue("application".equals(authToken.getApplication()));
        Assert.assertTrue("password".equals(authToken.getGrantType()));
        Assert.assertTrue("User123".equals(authToken.getPrincipal()));
        Assert.assertTrue("clientSecret".equals(authToken.getClientSecret()));
    }

    @Test
    public void testInValidGrantType() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");
        Mockito.when(req.getParameter("password")).thenReturn("Password123");
        OAuthAuthenticationToken authToken = oAuthResourceOwnerAuthenticationFilter.createAuthenticationToken(req, resp, "full", "application", "invalid_grant_type", null, "clientId", "clientSecret");
        Assert.assertTrue(authToken == null);
    }
}
