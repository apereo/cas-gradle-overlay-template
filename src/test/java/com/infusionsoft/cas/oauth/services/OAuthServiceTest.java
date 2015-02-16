package com.infusionsoft.cas.oauth.services;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.dto.OAuthApplication;
import com.infusionsoft.cas.oauth.exceptions.OAuthAccessDeniedException;
import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiClientService;
import com.infusionsoft.cas.oauth.mashery.api.domain.*;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import org.mockito.internal.util.reflection.Whitebox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class OAuthServiceTest {

    private static final String TEST_APP = "testApp";
    private static final String TEST_APP_HOST_NAME = "testApp.infusionsoft.com";
    private static final String TEST_INVALID_APP_HOST_NAME = "invalid.infusionsoft.com";
    private static final String TEST_STATE = "testState";
    private static final Long TEST_GLOBAL_USER_ID = 1L;
    private static final String TEST_USERNAME = "jojo@infusionsoft.com";
    private static final String TEST_USER_CONTEXT = TEST_GLOBAL_USER_ID + "|" + TEST_APP_HOST_NAME;
    private static final String TOKEN_1 = "token1";
    private static final String TOKEN_2 = "token2";
    private static final String TOKEN_3 = "token3";
    private static final String SERVICE_KEY = "serviceKey";
    private static final String CLIENT_ID = "client_id";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String TEST_REDIRECT_URI = "http://redirect.com";

    private OAuthService oAuthServiceToTest;
    private MasheryApiClientService masheryApiClientService;
    private CrmService crmService;
    private UserService userService;

    private Set<MasheryUserApplication> testAppSet;
    private UserAccount testAccount;

    @Before
    public void beforeMethod() {
        masheryApiClientService = mock(MasheryApiClientService.class);

        crmService = mock(CrmService.class);
        when(crmService.buildCrmHostName(TEST_APP)).thenReturn(TEST_APP_HOST_NAME);

        userService = mock(UserService.class);
        when(userService.validateUserApplication(TEST_APP_HOST_NAME)).thenReturn(true);
        when(userService.validateUserApplication(TEST_INVALID_APP_HOST_NAME)).thenReturn(false);

        createTestApps();

        oAuthServiceToTest = new OAuthService();

        Whitebox.setInternalState(oAuthServiceToTest, "masheryApiClientService", masheryApiClientService);
        Whitebox.setInternalState(oAuthServiceToTest, "crmService", crmService);
        Whitebox.setInternalState(oAuthServiceToTest, "userService", userService);

        setupUserAccount();
    }

    private void createTestApps() {
        testAppSet = new HashSet<MasheryUserApplication>();
        MasheryUserApplication app = new MasheryUserApplication();
        app.setName("ACME");
        app.setClient_id(CLIENT_ID);
        Set<String> accessTokens = new HashSet<String>();

        MasheryAccessToken masheryAccessToken1 = new MasheryAccessToken();
        masheryAccessToken1.setToken(TOKEN_1);

        MasheryAccessToken masheryAccessToken2 = new MasheryAccessToken();
        masheryAccessToken2.setToken(TOKEN_2);

        MasheryAccessToken masheryAccessToken3 = new MasheryAccessToken();
        masheryAccessToken3.setToken(TOKEN_3);

        accessTokens.add(TOKEN_1);
        accessTokens.add(TOKEN_2);
        accessTokens.add(TOKEN_3);

        app.setAccessTokens(accessTokens);

        testAppSet.add(app);
    }

    private void setupUserAccount() {
        testAccount = new UserAccount();
        testAccount.setAppName(TEST_APP);
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setId(TEST_GLOBAL_USER_ID);
        testAccount.setUser(user);
    }

    @Test
    public void testFetchOAuthApplication() throws Exception {
        final MasheryOAuthApplication masheryOAuthApplication = new MasheryOAuthApplication();
        masheryOAuthApplication.setId(1);
        when(masheryApiClientService.fetchOAuthApplication(anyString(), anyString(), anyString(), anyString())).thenReturn(masheryOAuthApplication);

        final MasheryApplication masheryApplication = new MasheryApplication();
        masheryApplication.setDescription("description");
        masheryApplication.setName("name");
        masheryApplication.setUsername("username");
        when(masheryApiClientService.fetchApplication(1)).thenReturn(masheryApplication);

        final MasheryMember masheryMember = new MasheryMember();
        masheryMember.setDisplayName("display name");
        when(masheryApiClientService.fetchMember("username")).thenReturn(masheryMember);

        // verify result
        final String responseType = "responseType";
        OAuthApplication oAuthApplication = oAuthServiceToTest.fetchApplication(SERVICE_KEY, CLIENT_ID, TEST_REDIRECT_URI, responseType);
        Assert.assertEquals(oAuthApplication.getDescription(), masheryApplication.getDescription());
        Assert.assertEquals(oAuthApplication.getDevelopedBy(), masheryMember.getDisplayName());
        Assert.assertEquals(oAuthApplication.getName(), masheryApplication.getName());

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchOAuthApplication(SERVICE_KEY, CLIENT_ID, TEST_REDIRECT_URI, responseType);
    }

    @Test
    public void testCreateAuthorizationCode() throws Exception {
        String code = "code";

        final MasheryUri masheryUri = new MasheryUri();
        masheryUri.setState("state");
        masheryUri.setUri("https://redirect.com?code=" + code);

        final MasheryAuthorizationCode masheryAuthorizationCodeInput = new MasheryAuthorizationCode();
        masheryAuthorizationCodeInput.setCode(code);
        masheryAuthorizationCodeInput.setUri(masheryUri);

        when(masheryApiClientService.createAuthorizationCode(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(masheryAuthorizationCodeInput);

        // verify result
        final String requestedScope = "requestedScope";
        String authorizationCode = oAuthServiceToTest.createAuthorizationCode(SERVICE_KEY, CLIENT_ID, requestedScope, TEST_APP_HOST_NAME, TEST_REDIRECT_URI, TEST_GLOBAL_USER_ID, TEST_STATE);
        Assert.assertEquals(authorizationCode, masheryAuthorizationCodeInput.getUri().getUri());

        // verify what happened for call to Mashery
        verify(userService, times(1)).validateUserApplication(TEST_APP_HOST_NAME);
        verify(masheryApiClientService, times(1)).createAuthorizationCode(SERVICE_KEY, CLIENT_ID, requestedScope + "|" + TEST_APP_HOST_NAME, TEST_REDIRECT_URI, TEST_USER_CONTEXT, TEST_STATE);
    }

    @Test(expected = OAuthAccessDeniedException.class)
    public void testCreateAuthorizationCodeWithInvalidApplication() throws Exception {
        oAuthServiceToTest.createAuthorizationCode(SERVICE_KEY, CLIENT_ID, "requestedScope", TEST_INVALID_APP_HOST_NAME, TEST_REDIRECT_URI, TEST_GLOBAL_USER_ID, TEST_STATE);
    }

    @Test
    public void testRevokeAccessToken() throws Exception {
        when(masheryApiClientService.revokeAccessToken(anyString(), anyString(), anyString())).thenReturn(true);

        // verify result
        Boolean wasSuccessful = oAuthServiceToTest.revokeAccessToken(SERVICE_KEY, CLIENT_ID, ACCESS_TOKEN);
        Assert.assertTrue(wasSuccessful);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).revokeAccessToken(SERVICE_KEY, CLIENT_ID, ACCESS_TOKEN);
    }

//    @Test
//    public void testFetchUserApplicationsByUserAccount() throws Exception {
//        when(masheryApiClientService.fetchUserApplicationsByUserContext(anyString(), any(TokenStatus.class))).thenReturn(testAppSet);
//
//        // verify result
//        Set<MasheryUserApplication> oauthAppsOutput = oAuthServiceToTest.fetchUserApplicationsByUserAccount(testAccount);
//        Assert.assertSame(oauthAppsOutput, testAppSet);
//
//        // verify what happened for call to Mashery
//        verify(masheryApiClientService, times(1)).fetchUserApplicationsByUserContext(TEST_USER_CONTEXT, TokenStatus.Active);
//    }

    @Test
    public void testRevokeAccessTokensByUserAccount() throws Exception {
        when(masheryApiClientService.fetchUserApplicationsByUserContext(anyString(), anyString())).thenReturn(testAppSet);
        when(masheryApiClientService.revokeAccessToken(anyString(), anyString(), anyString())).thenReturn(true);

        // verify result
        Boolean wasSuccessful = oAuthServiceToTest.revokeAccessTokensByUserAccount(SERVICE_KEY, testAccount);
        Assert.assertTrue(wasSuccessful);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchUserApplicationsByUserContext(SERVICE_KEY, TEST_USER_CONTEXT);
        verify(masheryApiClientService, times(1)).revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_1);
        verify(masheryApiClientService, times(1)).revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_2);
        verify(masheryApiClientService, times(1)).revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_3);
    }

    @Test
    public void testRevokeAccessTokensByUserAccountUnsuccessful() throws Exception {
        when(masheryApiClientService.fetchUserApplicationsByUserContext(anyString(), anyString())).thenReturn(testAppSet);
        when(masheryApiClientService.revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_1)).thenReturn(true);
        when(masheryApiClientService.revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_2)).thenReturn(false);
        when(masheryApiClientService.revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_3)).thenReturn(true);

        // verify result
        Boolean wasSuccessful = oAuthServiceToTest.revokeAccessTokensByUserAccount(SERVICE_KEY, testAccount);
        Assert.assertFalse(wasSuccessful);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchUserApplicationsByUserContext(SERVICE_KEY, TEST_USER_CONTEXT);
        verify(masheryApiClientService, times(1)).revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_1);
        verify(masheryApiClientService, times(1)).revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_2);
        verify(masheryApiClientService, times(1)).revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_3);
    }

}
