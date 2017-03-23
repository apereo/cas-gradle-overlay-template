package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OAuthResourceOwnerTokenProvider tokenProviderToTest = new OAuthResourceOwnerTokenProvider();

    @Before
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidToken() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");
        Mockito.when(req.getParameter("password")).thenReturn("Password123");
        OAuthResourceOwnerAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "password", new OAuthServiceConfig(), "clientId", "clientSecret");
        Assert.assertEquals("clientId", authToken.getClientId());
        Assert.assertEquals("full", authToken.getScope());
        Assert.assertEquals("application", authToken.getApplication());
        Assert.assertEquals("password", authToken.getGrantType());
        Assert.assertEquals("User123", authToken.getPrincipal());
        Assert.assertEquals("clientSecret", authToken.getClientSecret());
    }

    @Test
    public void testMissingClientId() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");
        Mockito.when(req.getParameter("password")).thenReturn("Password123");

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.clientId.missing");

        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "password", new OAuthServiceConfig(), "", "clientSecret");
    }

    @Test
    public void testMissingClientSecret() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");
        Mockito.when(req.getParameter("password")).thenReturn("Password123");

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.clientSecret");

        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "password", new OAuthServiceConfig(), "clientId", "");
    }

    @Test
    public void testMissingUsername() {
        Mockito.when(req.getParameter("password")).thenReturn("Password123");

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.username.missing");

        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "password", new OAuthServiceConfig(), "clientId", "clientSecret");
    }

    @Test
    public void testMissingPassword() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.password.missing");

        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "password", new OAuthServiceConfig(), "clientId", "clientSecret");
    }

    @Test
    public void testInvalidGrantType() {
        Mockito.when(req.getParameter("username")).thenReturn("User123");
        Mockito.when(req.getParameter("password")).thenReturn("Password123");
        OAuthResourceOwnerAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "invalid_grant_type", new OAuthServiceConfig(), "clientId", "clientSecret");
        Assert.assertTrue(authToken == null);
    }
}
