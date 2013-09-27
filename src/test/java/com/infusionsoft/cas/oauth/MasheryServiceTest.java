package com.infusionsoft.cas.oauth;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.domain.*;
import com.infusionsoft.cas.oauth.wrappers.*;
import com.infusionsoft.cas.services.CrmService;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@PrepareForTest({MasheryService.class})
@SuppressWarnings("unchecked")
public class MasheryServiceTest {

    private static final String SERVICE_KEY = "serviceKey";
    private static final String USER_CONTEXT = "userContext";
    private static final String TEST_APP = "testApp";
    private static final String TEST_APP_HOST_NAME = "testApp.infusionsoft.com";
    private static final String TEST_USERNAME = "jojo@infusionsoft.com";
    private static final String TEST_USER_CONTEXT = TEST_USERNAME + "|" + TEST_APP_HOST_NAME;
    private static final String TOKEN_1 = "token1";
    private static final String TOKEN_2 = "token2";
    private static final String TOKEN_3 = "token3";
    private static final String CLIENT_ID = "client_id";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String MOCKED_URL = "mockedUrl";
    private static final String TEST_REDIRECT_URI = "redirectUri";

    private RestTemplate restTemplate;
    private MasheryService masheryServiceToTest;
    private CrmService crmService;
    private Logger log;

    private WrappedMasheryUserApplication wrappedMasheryUserApplication;
    private UserAccount testAccount;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }

    @BeforeMethod
    public void beforeMethod() {
        crmService = mock(CrmService.class);
        when(crmService.buildCrmHostName(TEST_APP)).thenReturn(TEST_APP_HOST_NAME);

        createWrappedMasheryUserApplication();

        restTemplate = mock(RestTemplate.class);

        masheryServiceToTest = new MasheryService();
        masheryServiceToTest.setServiceKey(SERVICE_KEY);
        Whitebox.setInternalState(masheryServiceToTest, "restTemplate", restTemplate);
        Whitebox.setInternalState(masheryServiceToTest, "crmService", crmService);

        setupUserAccount();

        log = mock(Logger.class);
        Whitebox.setInternalState(masheryServiceToTest, "log", log);
    }

    private void createWrappedMasheryUserApplication() {
        wrappedMasheryUserApplication = new WrappedMasheryUserApplication();
        Set<MasheryUserApplication> userApps = new HashSet<MasheryUserApplication>();
        MasheryUserApplication app = new MasheryUserApplication();
        app.setName("ACME");
        app.setClient_id(CLIENT_ID);
        Set<String> accessTokens = new HashSet<String>();
        accessTokens.add(TOKEN_1);
        accessTokens.add(TOKEN_2);
        accessTokens.add(TOKEN_3);
        app.setAccess_tokens(accessTokens);

        userApps.add(app);

        wrappedMasheryUserApplication.setResult(userApps);
    }

    private void setupUserAccount() {
        testAccount = new UserAccount();
        testAccount.setAppName(TEST_APP);
        User user = new User();
        user.setUsername(TEST_USERNAME);
        testAccount.setUser(user);
    }

    /**
     * NOTE: this is the same scenario as {@link #testRevokeAccessTokensByUserAccount_spyBuildUrl()}.  The difference is what is being spied.
     */
    @Test
    public void testRevokeAccessTokensByUserAccount_spyFetchAndRevoke() throws Exception {
        // Spy it so we can mock fetchUserApplicationsByUserAccount and revokeAccessToken, which are tested elsewhere
        masheryServiceToTest = spy(masheryServiceToTest);
        doReturn(wrappedMasheryUserApplication.getResult()).when(masheryServiceToTest).fetchUserApplicationsByUserAccount(testAccount);
        doReturn(true).when(masheryServiceToTest).revokeAccessToken(anyString(), anyString(), anyString());

        Boolean wasSuccessful = masheryServiceToTest.revokeAccessTokensByUserAccount(testAccount);
        Assert.assertTrue(wasSuccessful);

        verify(masheryServiceToTest, times(1)).fetchUserApplicationsByUserAccount(testAccount);
        verify(masheryServiceToTest, times(1)).revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_1);
        verify(masheryServiceToTest, times(1)).revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_2);
        verify(masheryServiceToTest, times(1)).revokeAccessToken(SERVICE_KEY, CLIENT_ID, TOKEN_3);
    }

    /**
     * NOTE: this is the same scenario as {@link #testRevokeAccessTokensByUserAccount_spyFetchAndRevoke()}.  The difference is what is being spied.
     */
    @Test
    public void testRevokeAccessTokensByUserAccount_spyBuildUrl() throws Exception {
        // We have to spy here in order to have buildUrl() return a known value, so we can match on specific parameters for restTemplate.postForObject()
        masheryServiceToTest = spy(masheryServiceToTest);
        when(masheryServiceToTest.buildUrl()).thenReturn(MOCKED_URL);

        MasheryJsonRpcRequest masheryJsonRpcRequest = mock(MasheryJsonRpcRequest.class);
        List<Object> paramList = mock(List.class);
        when(masheryJsonRpcRequest.getParams()).thenReturn(paramList);
        HttpHeaders headers = mock(HttpHeaders.class);
        HttpEntity<MasheryJsonRpcRequest> request = mock(HttpEntity.class);

        PowerMockito.whenNew(MasheryJsonRpcRequest.class).withNoArguments().thenReturn(masheryJsonRpcRequest);
        PowerMockito.whenNew(HttpHeaders.class).withNoArguments().thenReturn(headers);
        PowerMockito.whenNew(HttpEntity.class).withArguments(masheryJsonRpcRequest, headers).thenReturn(request);

        when(crmService.buildCrmHostName(TEST_APP)).thenReturn(TEST_APP_HOST_NAME);
        when(restTemplate.postForObject(MOCKED_URL, request, WrappedMasheryUserApplication.class)).thenReturn(wrappedMasheryUserApplication);

        WrappedMasheryBoolean wrappedBooleanResult = new WrappedMasheryBoolean();
        wrappedBooleanResult.setResult(Boolean.TRUE);
        when(restTemplate.postForObject(MOCKED_URL, request, WrappedMasheryBoolean.class)).thenReturn(wrappedBooleanResult);

        Boolean wasSuccessful = masheryServiceToTest.revokeAccessTokensByUserAccount(testAccount);
        Assert.assertTrue(wasSuccessful);

        //verify what happened for call to fetch user apps from mashery
        verify(headers, times(4)).setContentType(MediaType.APPLICATION_JSON);   //once get get apps, 3 times to revoke tokens
        verify(masheryJsonRpcRequest).setMethod("oauth2.fetchUserApplications");
        verify(paramList, times(4)).add(SERVICE_KEY);   //once get get apps, 3 times to revoke tokens
        verify(paramList).add(TEST_USER_CONTEXT);

        //verify which happened for call to revoke all 3 tokens associated with mashery app
        verify(masheryJsonRpcRequest, times(3)).setMethod("oauth2.revokeAccessToken");
    }

    @Test
    public void testFetchUserApplicationsByUserContext() throws Exception {
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedMasheryUserApplication);

        Set<MasheryUserApplication> oauthApps = masheryServiceToTest.fetchUserApplicationsByUserContext(SERVICE_KEY, USER_CONTEXT, TokenStatus.Active);

        // verify result
        Assert.assertNotNull(oauthApps.iterator().next());
        Assert.assertEquals(oauthApps.iterator().next().getName(), wrappedMasheryUserApplication.getResult().iterator().next().getName());

        // verify what happened for call to Mashery
        verifyCallToMashery("oauth2.fetchUserApplications", Arrays.asList(new Object[]{SERVICE_KEY, USER_CONTEXT, TokenStatus.Active.getValue()}));
    }

    private void verifyCallToMashery(String expectedMethod, List<Object> expectedParams) throws NoSuchMethodException {
        // Verify the restTemplate call and capture the request object passed in
        ArgumentCaptor<HttpEntity> requestArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate, times(1)).postForObject(anyString(), requestArgumentCaptor.capture(), any(Class.class));
        // Verify that the JSON header was set
        HttpEntity<MasheryJsonRpcRequest> requestHttpEntity = (HttpEntity<MasheryJsonRpcRequest>) requestArgumentCaptor.getValue();
        Assert.assertEquals(requestHttpEntity.getHeaders().getContentType(), MediaType.APPLICATION_JSON);
        // Verify the MasheryJsonRpcRequest parameter values
        MasheryJsonRpcRequest masheryJsonRpcRequest = requestHttpEntity.getBody();
        Assert.assertEquals(masheryJsonRpcRequest.getMethod(), expectedMethod);
        final List<Object> actualParams = masheryJsonRpcRequest.getParams();
        Assert.assertEquals(actualParams.size(), expectedParams.size());
        for (int i = 0; i < actualParams.size(); i++) {
            final Object actualParam = actualParams.get(i);
            final Object expectedParam = expectedParams.get(i);
            Assert.assertEquals(actualParam.getClass(), expectedParam.getClass());
            // If the class has an equals method other than the one inherited from Object, use it to compare. Otherwise use EqualsBuilder.reflectionEquals().
            final Method equalsMethod = actualParam.getClass().getMethod("equals", Object.class);
            if (equalsMethod.getDeclaringClass().equals(Object.class)) {
                Assert.assertTrue(EqualsBuilder.reflectionEquals(actualParam, expectedParam));
            } else {
                Assert.assertEquals(actualParam, expectedParam);
            }
        }
    }

    @Test
    public void testFetchUserApplicationsByUserContext_throwsException() throws Exception {
        final RestClientException testException = new RestClientException("blah");
        doThrow(testException).when(restTemplate).postForObject(anyString(), anyObject(), any(Class.class));
        // Make sure the call doesn't mask the exception, and that it gets logged
        try {
            masheryServiceToTest.fetchUserApplicationsByUserContext(SERVICE_KEY, USER_CONTEXT, TokenStatus.Active);
            Assert.fail();
        } catch (RestClientException e) {
            Assert.assertEquals(e, testException);
        }
        ArgumentCaptor<RestClientException> exceptionArgumentCaptor = ArgumentCaptor.forClass(RestClientException.class);
        verify(log).error(anyString(), exceptionArgumentCaptor.capture());
        Assert.assertEquals(exceptionArgumentCaptor.getValue(), testException);
    }

    @Test
    public void testFetchUserApplicationsByUserAccount() throws Exception {
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedMasheryUserApplication);

        Set<MasheryUserApplication> oauthApps = masheryServiceToTest.fetchUserApplicationsByUserAccount(testAccount);

        // verify result
        Assert.assertNotNull(oauthApps.iterator().next());
        Assert.assertEquals(oauthApps.iterator().next().getName(), wrappedMasheryUserApplication.getResult().iterator().next().getName());

        // verify what happened for call to Mashery
        verifyCallToMashery("oauth2.fetchUserApplications", Arrays.asList(new Object[]{SERVICE_KEY, TEST_USER_CONTEXT, TokenStatus.Active.getValue()}));
    }

    @Test
    public void testRevokeAccessToken() throws Exception {
        WrappedMasheryBoolean wrappedBooleanResult = new WrappedMasheryBoolean();
        wrappedBooleanResult.setResult(Boolean.TRUE);
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedBooleanResult);

        // verify result
        Boolean wasSuccessful = masheryServiceToTest.revokeAccessToken(SERVICE_KEY, CLIENT_ID, ACCESS_TOKEN);
        Assert.assertTrue(wasSuccessful);

        // verify what happened for call to Mashery
        Map<String, String> expectedClientMap = new HashMap<String, String>();
        expectedClientMap.put("client_id", CLIENT_ID);
        verifyCallToMashery("oauth2.revokeAccessToken", Arrays.asList(new Object[]{SERVICE_KEY, expectedClientMap, ACCESS_TOKEN}));
    }

    @Test
    public void testFetchOAuthApplication() throws Exception {
        WrappedMasheryOAuthApplication wrappedMasheryOAuthApplication = new WrappedMasheryOAuthApplication();
        wrappedMasheryOAuthApplication.setResult(new MasheryOAuthApplication());
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedMasheryOAuthApplication);

        // verify result
        final String responseType = "responseType";
        MasheryOAuthApplication oAuthApplication = masheryServiceToTest.fetchOAuthApplication(CLIENT_ID, TEST_REDIRECT_URI, responseType);
        Assert.assertSame(oAuthApplication, wrappedMasheryOAuthApplication.getResult());

        // verify what happened for call to Mashery
        verifyCallToMashery("oauth2.fetchApplication", Arrays.asList(new Object[]{SERVICE_KEY, new MasheryClient(CLIENT_ID, ""), new MasheryUri(TEST_REDIRECT_URI, ""), responseType}));
    }

    @Test
    public void testFetchApplication() throws Exception {
        WrappedMasheryApplication wrappedMasheryApplication = new WrappedMasheryApplication();
        wrappedMasheryApplication.setResult(new MasheryApplication());
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedMasheryApplication);

        // verify result
        final int id = 1111;
        MasheryApplication application = masheryServiceToTest.fetchApplication(id);
        Assert.assertSame(application, wrappedMasheryApplication.getResult());

        // verify what happened for call to Mashery
        verifyCallToMashery("application.fetch", Arrays.asList(new Object[]{id}));
    }

    @Test
    public void testFetchMember() throws Exception {
        WrappedMasheryMember wrappedMasheryMember = new WrappedMasheryMember();
        wrappedMasheryMember.setResult(new MasheryMember());
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedMasheryMember);

        // verify result
        MasheryMember masheryMember = masheryServiceToTest.fetchMember(TEST_USERNAME);
        Assert.assertSame(masheryMember, wrappedMasheryMember.getResult());

        // verify what happened for call to Mashery
        verifyCallToMashery("member.fetch", Arrays.asList(new Object[]{TEST_USERNAME}));
    }

    @Test
    public void testFetchAccessToken() throws Exception {
        WrappedMasheryAccessToken wrappedMasheryAccessToken = new WrappedMasheryAccessToken();
        wrappedMasheryAccessToken.setResult(new MasheryAccessToken());
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedMasheryAccessToken);

        // verify result
        MasheryAccessToken masheryAccessToken = masheryServiceToTest.fetchAccessToken(SERVICE_KEY, ACCESS_TOKEN);
        Assert.assertSame(masheryAccessToken, wrappedMasheryAccessToken.getResult());

        // verify what happened for call to Mashery
        verifyCallToMashery("oauth2.fetchAccessToken", Arrays.asList(new Object[]{SERVICE_KEY, ACCESS_TOKEN}));
    }

    @Test
    public void testCreateAuthorizationCode() throws Exception {
        WrappedMasheryAuthorizationCode wrappedMasheryAuthorizationCode = new WrappedMasheryAuthorizationCode();
        wrappedMasheryAuthorizationCode.setResult(new MasheryAuthorizationCode());
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedMasheryAuthorizationCode);

        // verify result
        final String requestedScope = "requestedScope";
        MasheryAuthorizationCode MasheryAuthorizationCode = masheryServiceToTest.createAuthorizationCode(CLIENT_ID, requestedScope, TEST_APP_HOST_NAME, TEST_REDIRECT_URI, TEST_USERNAME);
        Assert.assertSame(MasheryAuthorizationCode, wrappedMasheryAuthorizationCode.getResult());

        // verify what happened for call to Mashery
        verifyCallToMashery("oauth2.createAuthorizationCode", Arrays.asList(new Object[]{SERVICE_KEY, new MasheryClient(CLIENT_ID, ""), new MasheryUri(TEST_REDIRECT_URI, ""), requestedScope + "|" + TEST_APP_HOST_NAME, TEST_USER_CONTEXT}));
    }

    @Test
    public void testBuildUrl() throws Exception {
        String apiKey = "apiKey", apiSecret = "apiSecret", apiUrl = "apiUrl", siteId = "siteId";
        Whitebox.setInternalState(masheryServiceToTest, "apiKey", apiKey);
        Whitebox.setInternalState(masheryServiceToTest, "apiSecret", apiSecret);
        Whitebox.setInternalState(masheryServiceToTest, "apiUrl", apiUrl);
        Whitebox.setInternalState(masheryServiceToTest, "siteId", siteId);

        final long epoch = 123456789L;
        String actualUrl = masheryServiceToTest.buildUrl(epoch);
        String expectedUrl = StringUtils.join(apiUrl, "/", siteId, "?apikey=", apiKey, "&sig=", DigestUtils.md5Hex(StringUtils.join(apiKey, apiSecret, epoch)));
        Assert.assertEquals(actualUrl, expectedUrl);
    }
}
