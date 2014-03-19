package com.infusionsoft.cas.oauth.services;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiClientService;
import com.infusionsoft.cas.oauth.mashery.api.domain.*;
import com.infusionsoft.cas.services.CrmService;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class OAuthServiceTest {

    private static final String TEST_APP = "testApp";
    private static final String TEST_APP_HOST_NAME = "testApp.infusionsoft.com";
    private static final String TEST_STATE = "testState";
    private static final Long TEST_GLOBAL_USER_ID = 1L;
    private static final String TEST_USERNAME = "jojo@infusionsoft.com";
    private static final String TEST_USER_CONTEXT = TEST_GLOBAL_USER_ID + "|" + TEST_APP_HOST_NAME;
    private static final String TOKEN_1 = "token1";
    private static final String TOKEN_2 = "token2";
    private static final String TOKEN_3 = "token3";
    private static final String CLIENT_ID = "client_id";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String TEST_REDIRECT_URI = "redirectUri";

    private OAuthService oAuthServiceToTest;
    private MasheryApiClientService masheryApiClientService;
    private CrmService crmService;

    private Set<MasheryUserApplication> testAppSet;
    private UserAccount testAccount;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }

    @BeforeMethod
    public void beforeMethod() {
        masheryApiClientService = mock(MasheryApiClientService.class);

        crmService = mock(CrmService.class);
        when(crmService.buildCrmHostName(TEST_APP)).thenReturn(TEST_APP_HOST_NAME);

        createTestApps();

        oAuthServiceToTest = new OAuthService();

        Whitebox.setInternalState(oAuthServiceToTest, "masheryApiClientService", masheryApiClientService);
        Whitebox.setInternalState(oAuthServiceToTest, "crmService", crmService);

        setupUserAccount();

    }

    private void createTestApps() {
        testAppSet = new HashSet<MasheryUserApplication>();
        MasheryUserApplication app = new MasheryUserApplication();
        app.setName("ACME");
        app.setClient_id(CLIENT_ID);
        Set<String> accessTokens = new HashSet<String>();
        accessTokens.add(TOKEN_1);
        accessTokens.add(TOKEN_2);
        accessTokens.add(TOKEN_3);
        app.setAccess_tokens(accessTokens);

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
        final MasheryOAuthApplication oAuthApplicationInput = new MasheryOAuthApplication();
        when(masheryApiClientService.fetchOAuthApplication(anyString(), anyString(), anyString())).thenReturn(oAuthApplicationInput);

        // verify result
        final String responseType = "responseType";
        MasheryOAuthApplication oAuthApplicationOutput = oAuthServiceToTest.fetchOAuthApplication(CLIENT_ID, TEST_REDIRECT_URI, responseType);
        Assert.assertSame(oAuthApplicationOutput, oAuthApplicationInput);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchOAuthApplication(CLIENT_ID, TEST_REDIRECT_URI, responseType);
    }

    @Test
    public void testFetchApplication() throws Exception {
        final MasheryApplication masheryApplicationInput = new MasheryApplication();
        when(masheryApiClientService.fetchApplication(anyInt())).thenReturn(masheryApplicationInput);

        // verify result
        final int id = 1111;
        MasheryApplication masheryApplicationOutput = oAuthServiceToTest.fetchApplication(id);
        Assert.assertSame(masheryApplicationOutput, masheryApplicationInput);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchApplication(id);
    }

    @Test
    public void testFetchMember() throws Exception {
        final MasheryMember masheryMemberInput = new MasheryMember();
        when(masheryApiClientService.fetchMember(anyString())).thenReturn(masheryMemberInput);

        // verify result
        MasheryMember masheryMemberOutput = oAuthServiceToTest.fetchMember(TEST_USERNAME);
        Assert.assertSame(masheryMemberOutput, masheryMemberInput);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchMember(TEST_USERNAME);
    }

    @Test
    public void testCreateAuthorizationCode() throws Exception {
        final MasheryAuthorizationCode masheryAuthorizationCodeInput = new MasheryAuthorizationCode();
        when(masheryApiClientService.createAuthorizationCode(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(masheryAuthorizationCodeInput);

        // verify result
        final String requestedScope = "requestedScope";
        MasheryAuthorizationCode masheryAuthorizationCodeOutput = oAuthServiceToTest.createAuthorizationCode(CLIENT_ID, requestedScope, TEST_APP_HOST_NAME, TEST_REDIRECT_URI, TEST_GLOBAL_USER_ID, TEST_STATE);
        Assert.assertSame(masheryAuthorizationCodeOutput, masheryAuthorizationCodeInput);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).createAuthorizationCode(CLIENT_ID, requestedScope + "|" + TEST_APP_HOST_NAME, TEST_REDIRECT_URI, TEST_USER_CONTEXT, TEST_STATE);
    }

    @Test
    public void testRevokeAccessToken() throws Exception {
        when(masheryApiClientService.revokeAccessToken(anyString(), anyString())).thenReturn(true);

        // verify result
        Boolean wasSuccessful = oAuthServiceToTest.revokeAccessToken(CLIENT_ID, ACCESS_TOKEN);
        Assert.assertTrue(wasSuccessful);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, ACCESS_TOKEN);
    }

    @Test
    public void testFetchAccessToken() throws Exception {
        final MasheryAccessToken masheryAccessTokenInput = new MasheryAccessToken();
        when(masheryApiClientService.fetchAccessToken(anyString())).thenReturn(masheryAccessTokenInput);

        // verify result
        MasheryAccessToken masheryAccessTokenOutput = oAuthServiceToTest.fetchAccessToken(ACCESS_TOKEN);
        Assert.assertSame(masheryAccessTokenOutput, masheryAccessTokenInput);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchAccessToken(ACCESS_TOKEN);
    }

    @Test
    public void testFetchUserApplicationsByUserAccount() throws Exception {
        when(masheryApiClientService.fetchUserApplicationsByUserContext(anyString(), any(TokenStatus.class))).thenReturn(testAppSet);

        // verify result
        Set<MasheryUserApplication> oauthAppsOutput = oAuthServiceToTest.fetchUserApplicationsByUserAccount(testAccount);
        Assert.assertSame(oauthAppsOutput, testAppSet);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchUserApplicationsByUserContext(TEST_USER_CONTEXT, TokenStatus.Active);
    }

    @Test
    public void testRevokeAccessTokensByUserAccount() throws Exception {
        when(masheryApiClientService.fetchUserApplicationsByUserContext(anyString(), any(TokenStatus.class))).thenReturn(testAppSet);
        when(masheryApiClientService.revokeAccessToken(anyString(), anyString())).thenReturn(true);

        // verify result
        Boolean wasSuccessful = oAuthServiceToTest.revokeAccessTokensByUserAccount(testAccount);
        Assert.assertTrue(wasSuccessful);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchUserApplicationsByUserContext(TEST_USER_CONTEXT, TokenStatus.Active);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_1);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_2);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_3);
    }

    @Test
    public void testRevokeAccessTokensByUserAccountUnsuccessful() throws Exception {
        when(masheryApiClientService.fetchUserApplicationsByUserContext(anyString(), any(TokenStatus.class))).thenReturn(testAppSet);
        when(masheryApiClientService.revokeAccessToken(CLIENT_ID, TOKEN_1)).thenReturn(true);
        when(masheryApiClientService.revokeAccessToken(CLIENT_ID, TOKEN_2)).thenReturn(false);
        when(masheryApiClientService.revokeAccessToken(CLIENT_ID, TOKEN_3)).thenReturn(true);

        // verify result
        Boolean wasSuccessful = oAuthServiceToTest.revokeAccessTokensByUserAccount(testAccount);
        Assert.assertFalse(wasSuccessful);

        // verify what happened for call to Mashery
        verify(masheryApiClientService, times(1)).fetchUserApplicationsByUserContext(TEST_USER_CONTEXT, TokenStatus.Active);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_1);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_2);
        verify(masheryApiClientService, times(1)).revokeAccessToken(CLIENT_ID, TOKEN_3);
    }

}
