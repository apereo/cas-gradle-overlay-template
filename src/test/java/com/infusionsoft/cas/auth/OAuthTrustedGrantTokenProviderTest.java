package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.doReturn;

public class OAuthTrustedGrantTokenProviderTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;

    private OAuthTrustedGrantTokenProvider tokenProviderToTest = new OAuthTrustedGrantTokenProvider();

    @Before
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidToken() {
        doReturn("1234").when(req).getParameter("global_user_id");
        OAuthTrustedGrantAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "urn:infusionsoft:params:oauth:grant-type:trusted", null, "clientId", "clientSecret");
        Assert.assertEquals("clientId", authToken.getClientId());
        Assert.assertEquals("full", authToken.getScope());
        Assert.assertEquals("application", authToken.getApplication());
        Assert.assertEquals("urn:infusionsoft:params:oauth:grant-type:trusted", authToken.getGrantType());
        Assert.assertNull(authToken.getPrincipal());
        Assert.assertEquals("clientSecret", authToken.getClientSecret());
        Assert.assertEquals(new Long(1234L), authToken.getGlobalUserId());
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testMissingGlobalUserId() {
        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "urn:infusionsoft:params:oauth:grant-type:trusted", null, "clientId", "clientSecret");
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testBadGlobalUserId() {
        doReturn("not a number").when(req).getParameter("global_user_id");
        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "urn:infusionsoft:params:oauth:grant-type:trusted", null, "clientId", "clientSecret");
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testMissingClientId() {
        doReturn("1234").when(req).getParameter("global_user_id");
        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "urn:infusionsoft:params:oauth:grant-type:trusted", null, "", "clientSecret");
    }

    @Test(expected = OAuthInvalidRequestException.class)
    public void testMissingClientSecret() {
        doReturn("1234").when(req).getParameter("global_user_id");
        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "urn:infusionsoft:params:oauth:grant-type:trusted", null, "clientId", "");
    }

    @Test
    public void testInvalidGrantType() {
        OAuthTrustedGrantAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "invalid_grant_type", null, "clientId", "clientSecret");
        Assert.assertTrue(authToken == null);
    }
}
