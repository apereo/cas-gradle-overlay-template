package com.infusionsoft.cas.oauth.mashery.api.client;

import com.infusionsoft.cas.oauth.exceptions.OAuthServerErrorException;
import com.infusionsoft.cas.oauth.mashery.api.domain.*;
import com.infusionsoft.cas.oauth.mashery.api.wrappers.*;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.springframework.http.HttpEntity;
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

@PrepareForTest({MasheryApiClientService.class})
@SuppressWarnings("unchecked")
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
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String TEST_REDIRECT_URI = "redirectUri";

    private MasheryApiClientService masheryServiceToTest;
    private RestTemplate restTemplate;
    private Logger log;

    private WrappedMasheryUserApplication wrappedMasheryUserApplication;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }

    @BeforeMethod
    public void beforeMethod() {

        createWrappedMasheryUserApplication();

        restTemplate = mock(RestTemplate.class);

        masheryServiceToTest = new MasheryApiClientService();
        Whitebox.setInternalState(masheryServiceToTest, "serviceKey", SERVICE_KEY);
        Whitebox.setInternalState(masheryServiceToTest, "restTemplate", restTemplate);

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

    @Test
    public void testFetchUserApplicationsByUserContext() throws Exception {
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedMasheryUserApplication);

        Set<MasheryUserApplication> oauthApps = masheryServiceToTest.fetchUserApplicationsByUserContext(USER_CONTEXT, TokenStatus.Active);

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
                Assert.assertTrue(EqualsBuilder.reflectionEquals(actualParam, expectedParam), "parameters should be equal");
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
            masheryServiceToTest.fetchUserApplicationsByUserContext(USER_CONTEXT, TokenStatus.Active);
            Assert.fail();
        } catch (OAuthServerErrorException e) {
            Assert.assertEquals(e.getCause(), testException);
        }
        ArgumentCaptor<RestClientException> exceptionArgumentCaptor = ArgumentCaptor.forClass(RestClientException.class);
        verify(log).error(anyString(), exceptionArgumentCaptor.capture());
        Assert.assertEquals(exceptionArgumentCaptor.getValue(), testException);
    }

    @Test
    public void testRevokeAccessToken() throws Exception {
        WrappedMasheryBoolean wrappedBooleanResult = new WrappedMasheryBoolean();
        wrappedBooleanResult.setResult(Boolean.TRUE);
        when(restTemplate.postForObject(anyString(), anyObject(), any(Class.class))).thenReturn(wrappedBooleanResult);

        // verify result
        Boolean wasSuccessful = masheryServiceToTest.revokeAccessToken(CLIENT_ID, ACCESS_TOKEN);
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
        MasheryAccessToken masheryAccessToken = masheryServiceToTest.fetchAccessToken(ACCESS_TOKEN);
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
        MasheryAuthorizationCode MasheryAuthorizationCode = masheryServiceToTest.createAuthorizationCode(CLIENT_ID, requestedScope, TEST_REDIRECT_URI, userContext, TEST_STATE);
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
}