package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.oauth.exceptions.OAuthAccessDeniedException;
import com.infusionsoft.cas.services.UserService;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class OAuthTicketGrantingTicketAuthenticationProviderTest {

    @InjectMocks
    private OAuthTicketGrantingTicketAuthenticationProvider providerToTest = new OAuthTicketGrantingTicketAuthenticationProvider();

    @Mock
    private UserService userService;

    @Mock
    private TicketGrantingTicket ticketGrantingTicket;

    @Mock
    private org.jasig.cas.authentication.Authentication mockAuthentication;

    @Mock
    private Principal principal;

    @Mock
    private OAuthServiceConfig oAuthServiceConfig;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String scope = "scope";
    private static final String application = "application";
    private static final String grantType = "urn:infusionsoft:params:oauth:grant-type:ticket-granting-ticket";
    private static final String clientId = "clientId";
    private static final String clientSecret = "clientSecret";
    private static final String trackingUUID = "trackingUUID";
    private static final String USERNAME = "username@infusiontest.com";
    private static final User user = new User();

    @Before
    public void setUp() throws Exception {
        user.setUsername(USERNAME);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAuthenticateSuccess() throws Exception {
        OAuthTicketGrantingTicketAuthenticationToken token = new OAuthTicketGrantingTicketAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, trackingUUID, ticketGrantingTicket);
        mockUser();

        Authentication actualReturn = providerToTest.authenticate(token);
        validateToken(actualReturn, false, token.getTicketGrantingTicket());
    }

    @Test
    public void testAuthenticateSuccessAnonymousNoTicketGrantingTicket() throws Exception {
        doReturn(true).when(oAuthServiceConfig).getAllowAnonymous();
        OAuthTicketGrantingTicketAuthenticationToken token = new OAuthTicketGrantingTicketAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, trackingUUID, null);
        mockUser();

        Authentication actualReturn = providerToTest.authenticate(token);
        validateToken(actualReturn, true, token.getTicketGrantingTicket());
    }

    @Test
    public void testAuthenticateSuccessAnonymousExpiredTicketGrantingTicket() throws Exception {
        doReturn(true).when(oAuthServiceConfig).getAllowAnonymous();
        doReturn(true).when(ticketGrantingTicket).isExpired();
        OAuthTicketGrantingTicketAuthenticationToken token = new OAuthTicketGrantingTicketAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, trackingUUID, ticketGrantingTicket);
        mockUser();

        Authentication actualReturn = providerToTest.authenticate(token);
        validateToken(actualReturn, true, token.getTicketGrantingTicket());
    }

    @Test
    public void testAuthenticateFail() throws Exception {
        OAuthTicketGrantingTicketAuthenticationToken token = new OAuthTicketGrantingTicketAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, trackingUUID, ticketGrantingTicket);
        mockUser();

        Authentication actualReturn = providerToTest.authenticate(token);
        validateToken(actualReturn, false, token.getTicketGrantingTicket());
    }

    @Test
    public void testAuthenticateFailNullToken() throws Exception {
        thrown.expect(NullPointerException.class);

        providerToTest.authenticate(null);
    }

    @Test
    public void testAuthenticateFailAnonymousNoTicketGrantingTicketAnonymousNotAllowedForService() throws Exception {
        doReturn(false).when(oAuthServiceConfig).getAllowAnonymous();
        OAuthTicketGrantingTicketAuthenticationToken token = new OAuthTicketGrantingTicketAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, trackingUUID, null);
        mockUser();

        thrown.expect(OAuthAccessDeniedException.class);
        thrown.expectMessage("oauth.exception.anonymous.not.allowed");

        providerToTest.authenticate(token);
    }

    @Test
    public void testAuthenticateFailAnonymousExpiredTicketGrantingTicketAnonymousNotAllowedForService() throws Exception {
        doReturn(false).when(oAuthServiceConfig).getAllowAnonymous();
        doReturn(true).when(ticketGrantingTicket).isExpired();
        OAuthTicketGrantingTicketAuthenticationToken token = new OAuthTicketGrantingTicketAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, trackingUUID, ticketGrantingTicket);
        mockUser();

        thrown.expect(OAuthAccessDeniedException.class);
        thrown.expectMessage("oauth.exception.anonymous.not.allowed");

        providerToTest.authenticate(token);
    }

    @Test
    public void testSupportsSuccess() throws Exception {
        Assert.assertTrue(providerToTest.supports(OAuthTicketGrantingTicketAuthenticationToken.class));
    }

    @Test
    public void testSupportsSuccessSubclass() throws Exception {
        // Mocking generates a subclass
        OAuthTicketGrantingTicketAuthenticationToken mockToken = mock(OAuthTicketGrantingTicketAuthenticationToken.class);
        Assert.assertTrue(providerToTest.supports(mockToken.getClass()));
    }

    @Test
    public void testSupportsFailSuperclass() throws Exception {
        Assert.assertFalse(providerToTest.supports(OAuthAuthenticationToken.class));
    }

    @Test
    public void testSupportsFailObject() throws Exception {
        Assert.assertFalse(providerToTest.supports(Object.class));
    }

    @Test
    public void testSupportsFailNull() throws Exception {
        thrown.expect(NullPointerException.class);

        providerToTest.supports(null);
    }

    private void mockUser() {
        doReturn(mockAuthentication).when(ticketGrantingTicket).getAuthentication();
        doReturn(principal).when(mockAuthentication).getPrincipal();
        doReturn(USERNAME).when(principal).getId();
        doReturn(user).when(userService).loadUser(USERNAME);
    }

    private void validateToken(Authentication actualReturn, boolean shouldBeAnonymous, TicketGrantingTicket expectedTicketGrantingTicket) {
        OAuthTicketGrantingTicketAuthenticationToken expectedReturn = new OAuthTicketGrantingTicketAuthenticationToken(oAuthServiceConfig, clientId, clientSecret, scope, grantType, application, trackingUUID, expectedTicketGrantingTicket);
        Assert.assertTrue(actualReturn instanceof OAuthTicketGrantingTicketAuthenticationToken);
        OAuthTicketGrantingTicketAuthenticationToken actualReturnToken = (OAuthTicketGrantingTicketAuthenticationToken) actualReturn;

        if (shouldBeAnonymous) {
            Assert.assertEquals(actualReturnToken.getPrincipal(), "anonymous-" + trackingUUID);
            Assert.assertFalse(actualReturnToken.isAuthenticated());
        } else {
            Assert.assertEquals(actualReturnToken.getPrincipal(), user);
            Assert.assertTrue(actualReturnToken.isAuthenticated());
        }

        Assert.assertEquals(actualReturnToken.getCredentials(), null);
        Assert.assertEquals(actualReturnToken.getServiceConfig(), expectedReturn.getServiceConfig());
        Assert.assertEquals(actualReturnToken.getClientId(), expectedReturn.getClientId());
        Assert.assertEquals(actualReturnToken.getClientSecret(), expectedReturn.getClientSecret());
        Assert.assertEquals(actualReturnToken.getScope(), expectedReturn.getScope());
        Assert.assertEquals(actualReturnToken.getGrantType(), expectedReturn.getGrantType());
        Assert.assertEquals(actualReturnToken.getApplication(), expectedReturn.getApplication());
        Assert.assertEquals(actualReturnToken.getTrackingUUID(), expectedReturn.getTrackingUUID());
        Assert.assertEquals(actualReturnToken.getTicketGrantingTicket(), expectedReturn.getTicketGrantingTicket());
    }

}
