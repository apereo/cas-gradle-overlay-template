package com.infusionsoft.cas.oauth.mashery.api.client;

import com.infusionsoft.cas.oauth.dto.OAuthGrantType;
import com.infusionsoft.cas.oauth.exceptions.OAuthServerErrorException;
import com.infusionsoft.cas.oauth.mashery.api.domain.*;
import com.infusionsoft.cas.oauth.mashery.api.wrappers.*;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MasheryApiClientService.class, LoggerFactory.class})
public class MasheryApiClientServiceTest {

    private static final String SERVICE_KEY = "serviceKey";
    private static final String USER_CONTEXT = "userContext";
    private static final String TEST_APP_HOST_NAME = "testApp.infusionsoft.com";
    private static final String TEST_STATE = "testState";
    private static final String TEST_USERNAME = "jojo@infusionsoft.com";
    private static final String TOKEN_1 = "token1";
    private static final String TOKEN_2 = "token2";
    private static final String TOKEN_3 = "token3";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "clientSecret";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String TEST_REDIRECT_URI = "redirectUri";
    private static final String TOKEN_TYPE = "token_type";
    private static final Integer EXPIRES_IN = 123456;
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String SCOPE = "scope";

    @InjectMocks
    private MasheryApiClientService masheryServiceToTest;

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity<MasheryJsonRpcRequest>> requestCaptor;

    private WrappedMasheryUserApplication wrappedMasheryUserApplication;

    @Before
    public void beforeMethod() {
        createWrappedMasheryUserApplication();

        masheryServiceToTest = new MasheryApiClientService();
        MockitoAnnotations.initMocks(this);
    }

    private void createWrappedMasheryUserApplication() {
        wrappedMasheryUserApplication = new WrappedMasheryUserApplication();
        Set<MasheryUserApplication> userApps = new HashSet<>();
        MasheryUserApplication app = new MasheryUserApplication();
        app.setName("ACME");
        app.setClient_id(CLIENT_ID);
        Set<String> accessTokens = new HashSet<>();
        accessTokens.add(TOKEN_1);
        accessTokens.add(TOKEN_2);
        accessTokens.add(TOKEN_3);
        app.setAccessTokens(accessTokens);

        userApps.add(app);

        wrappedMasheryUserApplication.setResult(userApps);
    }

    @Test
    public void testFetchUserApplicationsByUserContext() throws Exception {
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedMasheryUserApplication);

        Set<MasheryUserApplication> oauthApps = masheryServiceToTest.fetchUserApplicationsByUserContext(SERVICE_KEY, USER_CONTEXT);

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
                Assert.assertTrue("parameters should be equal", EqualsBuilder.reflectionEquals(actualParam, expectedParam));
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
            masheryServiceToTest.fetchUserApplicationsByUserContext(SERVICE_KEY, USER_CONTEXT);
            Assert.fail();
        } catch (OAuthServerErrorException e) {
            Assert.assertEquals(e.getCause(), testException);
        }
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
        MasheryOAuthApplication oAuthApplication = masheryServiceToTest.fetchOAuthApplication(SERVICE_KEY, CLIENT_ID, TEST_REDIRECT_URI, responseType);
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
        Set<MasheryMember> masheryMembers = new HashSet<MasheryMember>();
        masheryMembers.add(new MasheryMember());

        MasheryQueryResult<MasheryMember> masheryQueryResult = new MasheryQueryResult<MasheryMember>();
        masheryQueryResult.setItems(masheryMembers);
        masheryQueryResult.setTotalItems(masheryMembers.size());

        WrappedMasheryMemberQueryResult wrappedMasheryMemberQueryResult = new WrappedMasheryMemberQueryResult();
        wrappedMasheryMemberQueryResult.setResult(masheryQueryResult);
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedMasheryMemberQueryResult);

        // verify result
        MasheryMember masheryMember = masheryServiceToTest.fetchMember(TEST_USERNAME);
        Assert.assertTrue(wrappedMasheryMemberQueryResult.getResult().getItems().contains(masheryMember));

        // verify what happened for call to Mashery
        verifyCallToMashery("object.query", Arrays.asList(new Object[]{"SELECT *, roles FROM members WHERE username='" + TEST_USERNAME + "'"}));
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
        final String requestedScope = "requestedScope" + "|" + TEST_APP_HOST_NAME;
        final String userContext = TEST_USERNAME + "|" + TEST_APP_HOST_NAME;
        MasheryAuthorizationCode MasheryAuthorizationCode = masheryServiceToTest.createAuthorizationCode(SERVICE_KEY, CLIENT_ID, requestedScope, TEST_REDIRECT_URI, userContext, TEST_STATE);
        Assert.assertSame(MasheryAuthorizationCode, wrappedMasheryAuthorizationCode.getResult());

        // verify what happened for call to Mashery
        verifyCallToMashery("oauth2.createAuthorizationCode", Arrays.asList(new Object[]{SERVICE_KEY, new MasheryClient(CLIENT_ID, ""), new MasheryUri(TEST_REDIRECT_URI, TEST_STATE), requestedScope, userContext}));
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

    @Test
    public void testCreateAccessTokenTrusted() throws Exception {
        final String grantType = OAuthGrantType.EXTENDED_TRUSTED.getValue();

        final WrappedMasheryCreateAccessTokenResponse generatedResponseWrapper = createAccessTokenResponse();
        doReturn(generatedResponseWrapper).when(restTemplate).postForObject(anyString(), any(), any());

        final MasheryCreateAccessTokenResponse returnedResponse = masheryServiceToTest.createAccessToken(SERVICE_KEY, CLIENT_ID, CLIENT_SECRET, grantType, SCOPE, USER_CONTEXT, REFRESH_TOKEN);
        Assert.assertNotNull(returnedResponse);
        Assert.assertSame(generatedResponseWrapper.getResult(), returnedResponse);
        Assert.assertEquals(returnedResponse.getAccess_token(), ACCESS_TOKEN);
        Assert.assertEquals(returnedResponse.getToken_type(), TOKEN_TYPE);
        Assert.assertEquals(returnedResponse.getExpires_in(), EXPIRES_IN);
        Assert.assertEquals(returnedResponse.getRefresh_token(), REFRESH_TOKEN);
        Assert.assertEquals(returnedResponse.getScope(), SCOPE);

        // Spoof password grant type for extended grants
        verifyRequest(OAuthGrantType.RESOURCE_OWNER_CREDENTIALS.getValue());
    }

    @Test
    public void testCreateAccessTokenRefresh() throws Exception {
        final String grantType = OAuthGrantType.REFRESH.getValue();

        final WrappedMasheryCreateAccessTokenResponse generatedResponseWrapper = createAccessTokenResponse();
        doReturn(generatedResponseWrapper).when(restTemplate).postForObject(anyString(), any(), any());

        final MasheryCreateAccessTokenResponse returnedResponse = masheryServiceToTest.createAccessToken(SERVICE_KEY, CLIENT_ID, CLIENT_SECRET, grantType, SCOPE, USER_CONTEXT, REFRESH_TOKEN);
        Assert.assertNotNull(returnedResponse);
        Assert.assertSame(generatedResponseWrapper.getResult(), returnedResponse);
        Assert.assertEquals(returnedResponse.getAccess_token(), ACCESS_TOKEN);
        Assert.assertEquals(returnedResponse.getToken_type(), TOKEN_TYPE);
        Assert.assertEquals(returnedResponse.getExpires_in(), EXPIRES_IN);
        Assert.assertEquals(returnedResponse.getRefresh_token(), REFRESH_TOKEN);
        Assert.assertEquals(returnedResponse.getScope(), SCOPE);

        verifyRequest(grantType);
    }

    @Test
    public void testCreateAccessTokenResourceOwner() throws Exception {
        final String grantType = OAuthGrantType.RESOURCE_OWNER_CREDENTIALS.getValue();

        final WrappedMasheryCreateAccessTokenResponse generatedResponseWrapper = createAccessTokenResponse();
        doReturn(generatedResponseWrapper).when(restTemplate).postForObject(anyString(), any(), any());

        final MasheryCreateAccessTokenResponse returnedResponse = masheryServiceToTest.createAccessToken(SERVICE_KEY, CLIENT_ID, CLIENT_SECRET, grantType, SCOPE, USER_CONTEXT, REFRESH_TOKEN);
        Assert.assertNotNull(returnedResponse);
        Assert.assertSame(generatedResponseWrapper.getResult(), returnedResponse);
        Assert.assertEquals(returnedResponse.getAccess_token(), ACCESS_TOKEN);
        Assert.assertEquals(returnedResponse.getToken_type(), TOKEN_TYPE);
        Assert.assertEquals(returnedResponse.getExpires_in(), EXPIRES_IN);
        Assert.assertEquals(returnedResponse.getRefresh_token(), REFRESH_TOKEN);
        Assert.assertEquals(returnedResponse.getScope(), SCOPE);

        verifyRequest(grantType);
    }

    @Test
    public void testCreateAccessTokenClientCredentials() throws Exception {
        final String grantType = OAuthGrantType.CLIENT_CREDENTIALS.getValue();

        final WrappedMasheryCreateAccessTokenResponse generatedResponseWrapper = createAccessTokenResponse();
        doReturn(generatedResponseWrapper).when(restTemplate).postForObject(anyString(), any(), any());

        final MasheryCreateAccessTokenResponse returnedResponse = masheryServiceToTest.createAccessToken(SERVICE_KEY, CLIENT_ID, CLIENT_SECRET, grantType, SCOPE, USER_CONTEXT, REFRESH_TOKEN);
        Assert.assertNotNull(returnedResponse);
        Assert.assertSame(generatedResponseWrapper.getResult(), returnedResponse);
        Assert.assertEquals(returnedResponse.getAccess_token(), ACCESS_TOKEN);
        Assert.assertEquals(returnedResponse.getToken_type(), TOKEN_TYPE);
        Assert.assertEquals(returnedResponse.getExpires_in(), EXPIRES_IN);
        Assert.assertNull(returnedResponse.getRefresh_token());
        Assert.assertEquals(returnedResponse.getScope(), SCOPE);

        verifyRequest(grantType);
    }

    @Test
    public void testCreateAccessTokenTicketGrantingTicket() throws Exception {
        final String grantType = OAuthGrantType.EXTENDED_TICKET_GRANTING_TICKET.getValue();

        final WrappedMasheryCreateAccessTokenResponse generatedResponseWrapper = createAccessTokenResponse();
        doReturn(generatedResponseWrapper).when(restTemplate).postForObject(anyString(), any(), any());

        final MasheryCreateAccessTokenResponse returnedResponse = masheryServiceToTest.createAccessToken(SERVICE_KEY, CLIENT_ID, CLIENT_SECRET, grantType, SCOPE, USER_CONTEXT, REFRESH_TOKEN);
        Assert.assertNotNull(returnedResponse);
        Assert.assertSame(generatedResponseWrapper.getResult(), returnedResponse);
        Assert.assertEquals(returnedResponse.getAccess_token(), ACCESS_TOKEN);
        Assert.assertEquals(returnedResponse.getToken_type(), TOKEN_TYPE);
        Assert.assertEquals(returnedResponse.getExpires_in(), EXPIRES_IN);
        Assert.assertNull(returnedResponse.getRefresh_token());
        Assert.assertEquals(returnedResponse.getScope(), SCOPE);

        // Spoof password grant type for extended grants
        verifyRequest(OAuthGrantType.RESOURCE_OWNER_CREDENTIALS.getValue());
    }

    private WrappedMasheryCreateAccessTokenResponse createAccessTokenResponse() {
        MasheryCreateAccessTokenResponse masheryCreateAccessTokenResponse = new MasheryCreateAccessTokenResponse();
        masheryCreateAccessTokenResponse.setAccess_token(ACCESS_TOKEN);
        masheryCreateAccessTokenResponse.setToken_type(TOKEN_TYPE);
        masheryCreateAccessTokenResponse.setExpires_in(EXPIRES_IN);
        masheryCreateAccessTokenResponse.setRefresh_token(REFRESH_TOKEN);
        masheryCreateAccessTokenResponse.setScope(SCOPE);

        WrappedMasheryCreateAccessTokenResponse wrappedMasheryCreateAccessTokenResponse = new WrappedMasheryCreateAccessTokenResponse();
        wrappedMasheryCreateAccessTokenResponse.setResult(masheryCreateAccessTokenResponse);
        return wrappedMasheryCreateAccessTokenResponse;
    }

    private void verifyRequest(String grantType) {
        verify(restTemplate, times(1)).postForObject(anyString(), requestCaptor.capture(), eq(WrappedMasheryCreateAccessTokenResponse.class));

        final HttpEntity<MasheryJsonRpcRequest> requestHttpEntity = requestCaptor.getValue();
        final MasheryJsonRpcRequest masheryJsonRpcRequest = requestHttpEntity.getBody();
        Assert.assertEquals("oauth2.createAccessToken", masheryJsonRpcRequest.getMethod());

        final List<Object> params = masheryJsonRpcRequest.getParams();
        Assert.assertEquals(SERVICE_KEY, params.get(0));
        Assert.assertTrue(params.get(1) instanceof MasheryClient);
        final MasheryClient masheryClient = (MasheryClient) params.get(1);
        Assert.assertEquals(CLIENT_ID, masheryClient.getClientId());
        Assert.assertEquals(CLIENT_SECRET, masheryClient.getClientSecret());
        Assert.assertTrue(SERVICE_KEY, params.get(2) instanceof MasheryTokenData);
        final MasheryTokenData masheryTokenData = (MasheryTokenData) params.get(2);
        Assert.assertEquals(grantType, masheryTokenData.getGrant_type());
        Assert.assertEquals(SCOPE, masheryTokenData.getScope());
        Assert.assertNull(masheryTokenData.getCode());
        Assert.assertNull(masheryTokenData.getResponse_type());
        Assert.assertEquals(REFRESH_TOKEN, masheryTokenData.getRefresh_token());
        Assert.assertTrue(SERVICE_KEY, params.get(3) instanceof MasheryUri);
        Assert.assertEquals(USER_CONTEXT, params.get(4));

        Assert.assertEquals(MediaType.APPLICATION_JSON, requestHttpEntity.getHeaders().getContentType());
    }

}