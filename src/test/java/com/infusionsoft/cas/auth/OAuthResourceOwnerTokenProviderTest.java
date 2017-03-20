package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
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

public class OAuthResourceOwnerTokenProviderTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;

    private OAuthResourceOwnerTokenProvider tokenProviderToTest = new OAuthResourceOwnerTokenProvider();

    @Before
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidToken() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");
        Mockito.when(req.getParameter("password")).thenReturn("Password123");
        OAuthResourceOwnerAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "password", null, "clientId", "clientSecret");
        Assert.assertEquals("clientId", authToken.getClientId());
        Assert.assertEquals("full", authToken.getScope());
        Assert.assertEquals("application", authToken.getApplication());
        Assert.assertEquals("password", authToken.getGrantType());
        Assert.assertEquals("User123",authToken.getPrincipal());
        Assert.assertEquals("clientSecret", authToken.getClientSecret());
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testMissingClientId() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");
        Mockito.when(req.getParameter("password")).thenReturn("Password123");
        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "password", null, "", "clientSecret");
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testMissingClientSecret() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");
        Mockito.when(req.getParameter("password")).thenReturn("Password123");
        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "password", null, "clientId", "");
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testMissingUsername() {
        Mockito.when(req.getParameter("password")).thenReturn("Password123");
        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "password", null, "clientId", "clientSecret");
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testMissingPassword() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");
        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "password", null, "clientId", "clientSecret");
    }

    @Test
    public void testInvalidGrantType() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");
        Mockito.when(req.getParameter("password")).thenReturn("Password123");
        OAuthResourceOwnerAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "invalid_grant_type", null, "clientId", "clientSecret");
        Assert.assertTrue(authToken == null);
    }
}
