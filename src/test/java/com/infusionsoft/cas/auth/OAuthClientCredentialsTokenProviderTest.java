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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OAuthClientCredentialsTokenProvider tokenProviderToTest = new OAuthClientCredentialsTokenProvider();

    @Before
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidToken() {
        OAuthClientCredentialsAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, new OAuthServiceConfig(), clientId, clientSecret);
        Assert.assertEquals(clientId, authToken.getClientId());
        Assert.assertEquals(scope, authToken.getScope());
        Assert.assertEquals(application, authToken.getApplication());
        Assert.assertEquals(grantType, authToken.getGrantType());
        Assert.assertNull(authToken.getPrincipal());
        Assert.assertEquals(clientSecret, authToken.getClientSecret());
    }

    @Test
    public void testInvalidGrantType() {
        OAuthAuthenticationToken authToken = tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, "invalid_grant_type", new OAuthServiceConfig(), clientId, clientSecret);
        Assert.assertTrue(authToken == null);
    }

    @Test
    public void testMissingClientId() {
        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.clientId.missing");

        tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, new OAuthServiceConfig(), "", clientSecret);
    }

    @Test
    public void testMissingClientSecret() {
        thrown.expect(OAuthInvalidRequestException.class);
        thrown.expectMessage("oauth.exception.clientSecret.missing");

        tokenProviderToTest.createAuthenticationToken(req, resp, scope, application, grantType, new OAuthServiceConfig(), clientId, "");
    }

}
