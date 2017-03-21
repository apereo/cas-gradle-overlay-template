package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthClient;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import com.infusionsoft.cas.services.OAuthClientService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class OAuthRefreshTokenProviderTest {
    private static final String scope = "full";
    private static final String application = "application";
    private static final String grantType = "refresh_token";
    private static final String clientId = "clientId";
    private static final String clientSecret = "clientSecret";

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private OAuthClientService oAuthClientService;

    @Mock
    private OAuthClient oAuthClient;

    @InjectMocks
    private OAuthRefreshTokenProvider tokenProviderToTest = new OAuthRefreshTokenProvider();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidToken() {
        doReturn(" refresh_token ").when(req).getParameter("refresh_token");
        OAuthRefreshAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, null, clientId, clientSecret);
        Assert.assertEquals(clientId, authToken.getClientId());
        Assert.assertNull(authToken.getScope());
        Assert.assertNull(authToken.getApplication());
        Assert.assertEquals(grantType, authToken.getGrantType());
        Assert.assertNull(authToken.getPrincipal());
        Assert.assertEquals(clientSecret, authToken.getClientSecret());
        Assert.assertEquals("refresh_token", authToken.getRefreshToken());
        verify(resp, never()).setHeader(anyString(), anyString());
    }

    @Test
    public void testValidTokenWithOrigin() {
        doReturn(" refresh_token ").when(req).getParameter("refresh_token");
        doReturn("originHeader").when(req).getHeader("Origin");
        OAuthRefreshAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, null, clientId, clientSecret);
        Assert.assertEquals(clientId, authToken.getClientId());
        Assert.assertNull(authToken.getScope());
        Assert.assertNull(authToken.getApplication());
        Assert.assertEquals(grantType, authToken.getGrantType());
        Assert.assertNull(authToken.getPrincipal());
        Assert.assertEquals(clientSecret, authToken.getClientSecret());
        Assert.assertEquals("refresh_token", authToken.getRefreshToken());

        verify(resp, times(1)).setHeader("Access-Control-Allow-Origin", "originHeader");
        verify(resp, times(1)).setHeader("Access-Control-Allow-Credentials", "true");
        verify(resp, times(1)).setHeader("Access-Control-Allow-Methods", "POST");
        verify(resp, times(1)).setHeader("Access-Control-Allow-Headers", "Content-Type, Authentication");
        verify(resp, times(1)).setHeader("Access-Control-Max-Age", "300");
    }

    @Test
    public void testValidTokenFromRegisteredClient() {
        doReturn(" refresh_token ").when(req).getParameter("refresh_token");
        doReturn(oAuthClient).when(oAuthClientService).loadOAuthClient(clientId);
        doReturn("lookedUpClientSecret").when(oAuthClient).getClientSecret();
        OAuthRefreshAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, null, clientId, "");
        Assert.assertEquals(clientId, authToken.getClientId());
        Assert.assertNull(authToken.getScope());
        Assert.assertNull(authToken.getApplication());
        Assert.assertEquals(grantType, authToken.getGrantType());
        Assert.assertNull(authToken.getPrincipal());
        Assert.assertEquals("lookedUpClientSecret", authToken.getClientSecret());
        Assert.assertEquals("refresh_token", authToken.getRefreshToken());
    }

    @Test
    public void testInvalidGrantType() {
        OAuthRefreshAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, "invalid_grant_type", null, clientId, clientSecret);
        Assert.assertTrue(authToken == null);
    }

    @Test
    public void testMissingRefreshToken() {
        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.refreshToken.missing");

        tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, null, clientId, clientSecret);
    }

    @Test
    public void testMissingClientId() {
        doReturn("refresh_token").when(req).getParameter("refresh_token");

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.clientId.missing");

        tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, null, "", clientSecret);
    }

    @Test
    public void testMissingClientSecret() {
        doReturn("refresh_token").when(req).getParameter("refresh_token");

        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.clientSecret.missing");

        tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, null, clientId, "");
    }

}
