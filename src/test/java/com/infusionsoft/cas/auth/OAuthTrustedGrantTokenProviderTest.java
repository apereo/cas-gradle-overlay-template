package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OAuthTrustedGrantTokenProvider tokenProviderToTest = new OAuthTrustedGrantTokenProvider();

    @Before
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidToken() {
        doReturn("1234").when(req).getParameter("global_user_id");
        OAuthTrustedGrantAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "urn:infusionsoft:params:oauth:grant-type:trusted", new OAuthServiceConfig(), "clientId", "clientSecret");
        Assert.assertEquals("clientId", authToken.getClientId());
        Assert.assertEquals("full", authToken.getScope());
        Assert.assertEquals("application", authToken.getApplication());
        Assert.assertEquals("urn:infusionsoft:params:oauth:grant-type:trusted", authToken.getGrantType());
        Assert.assertNull(authToken.getPrincipal());
        Assert.assertEquals("clientSecret", authToken.getClientSecret());
        Assert.assertEquals(new Long(1234L), authToken.getGlobalUserId());
    }

    @Test
    public void testMissingGlobalUserId() {
        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.userId.missing");

        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "urn:infusionsoft:params:oauth:grant-type:trusted", new OAuthServiceConfig(), "clientId", "clientSecret");
    }

    @Test
    public void testBadGlobalUserId() {
        doReturn("not a number").when(req).getParameter("global_user_id");

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.userId.bad");

        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "urn:infusionsoft:params:oauth:grant-type:trusted", new OAuthServiceConfig(), "clientId", "clientSecret");
    }

    @Test
    public void testMissingClientId() {
        doReturn("1234").when(req).getParameter("global_user_id");

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.clientId.missing");

        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "urn:infusionsoft:params:oauth:grant-type:trusted", new OAuthServiceConfig(), "", "clientSecret");
    }

    @Test
    public void testMissingClientSecret() {
        doReturn("1234").when(req).getParameter("global_user_id");

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.clientSecret.missing");

        tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "urn:infusionsoft:params:oauth:grant-type:trusted", new OAuthServiceConfig(), "clientId", "");
    }

    @Test
    public void testInvalidGrantType() {
        OAuthTrustedGrantAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, "full", "application", "invalid_grant_type", new OAuthServiceConfig(), "clientId", "clientSecret");
        Assert.assertTrue(authToken == null);
    }
}
