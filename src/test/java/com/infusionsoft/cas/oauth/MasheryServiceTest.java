package com.infusionsoft.cas.oauth;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.domain.MasheryUserApplication;
import com.infusionsoft.cas.oauth.wrappers.WrappedMasheryBoolean;
import com.infusionsoft.cas.oauth.wrappers.WrappedMasheryUserApplication;
import com.infusionsoft.cas.services.CrmService;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PrepareForTest({Logger.class, MasheryJsonRpcRequest.class, HttpHeaders.class, HttpEntity.class, MasheryService.class})
public class MasheryServiceTest   {
    private RestTemplate restTemplate;
    private MasheryService classToTest;
    private WrappedMasheryUserApplication wrappedMasheryUserApplication;
    private Logger log;
    private CrmService crmService;
    private MasheryJsonRpcRequest masheryJsonRpcRequest;
    private HttpHeaders headers;
    private HttpEntity<MasheryJsonRpcRequest> request;

    @BeforeTest
    public void beforeTest() throws Exception {
        log = Mockito.mock(Logger.class);
        PowerMockito.mockStatic(Logger.class);
        Mockito.when(Logger.getLogger(Mockito.any(Class.class))).thenReturn(log);
    }

    @BeforeMethod
    public void beforeMethod(){
        crmService = Mockito.mock(CrmService.class);
        masheryJsonRpcRequest = Mockito.mock(MasheryJsonRpcRequest.class);
        headers = Mockito.mock(HttpHeaders.class);
        request = Mockito.mock(HttpEntity.class);
        restTemplate = Mockito.mock(RestTemplate.class);

        classToTest = PowerMockito.spy(new MasheryService());   //have to spy buildUrl because it uses a timestamp as part of the signature

        classToTest.setServiceKey("serviceKey");
        Mockito.when(classToTest.buildUrl()).thenReturn("mockedUrl");
        Whitebox.setInternalState(classToTest, "restTemplate", restTemplate);
        Whitebox.setInternalState(classToTest, "crmService", crmService);
        createWrappedMasheryUserApplication();
    }

    @Test
    public void testRevokeAccessTokenByUserAccount() throws Exception{
        List<Object> paramList = Mockito.mock(List.class);
        Mockito.when(masheryJsonRpcRequest.getParams()).thenReturn(paramList);

        PowerMockito.whenNew(MasheryJsonRpcRequest.class).withNoArguments().thenReturn(masheryJsonRpcRequest);
        PowerMockito.whenNew(HttpHeaders.class).withNoArguments().thenReturn(headers);
        PowerMockito.whenNew(HttpEntity.class).withArguments(masheryJsonRpcRequest, headers).thenReturn(request);

        Mockito.when(crmService.buildCrmHostName("testApp")).thenReturn("testApp@infusionsoft.com");
        Mockito.when(restTemplate.postForObject("mockedUrl", request, WrappedMasheryUserApplication.class)).thenReturn(wrappedMasheryUserApplication);


        WrappedMasheryBoolean wrappedBooleanResult = new WrappedMasheryBoolean();
        wrappedBooleanResult.setResult(Boolean.TRUE);
        Mockito.when(restTemplate.postForObject("mockedUrl", request, WrappedMasheryBoolean.class)).thenReturn(wrappedBooleanResult);

        Boolean wasSuccessful = classToTest.revokeAccessTokensByUserAccount(getUserAccount());
        Assert.assertTrue(wasSuccessful);

        //verify what happened for call to fetch user apps from mashery
        Mockito.verify(headers, Mockito.times(4)).setContentType(MediaType.APPLICATION_JSON);   //once get get apps, 3 times to revoke tokens
        Mockito.verify(masheryJsonRpcRequest).setMethod("oauth2.fetchUserApplications");
        Mockito.verify(paramList, Mockito.times(4)).add("serviceKey");   //once get get apps, 3 times to revoke tokens
        Mockito.verify(paramList).add("jojo@infusionsoft.com|testApp@infusionsoft.com");

        //verify which happened for call to revoke all 3 tokens associated with mashery app
        Mockito.verify(masheryJsonRpcRequest, Mockito.times(3)).setMethod("oauth2.revokeAccessToken");
    }


    @Test
    public void testFetchUserApplicationsByUserContext() {
        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.any(Class.class))).thenReturn(wrappedMasheryUserApplication);
        Set<MasheryUserApplication> oauthApps = classToTest.fetchUserApplicationsByUserContext("serviceKey", "userContext", TokenStatus.Active);
        Assert.assertTrue(oauthApps.iterator().next() != null);
        Assert.assertTrue(oauthApps.iterator().next().getName().equals(wrappedMasheryUserApplication.getResult().iterator().next().getName()));
    }

    @Test
    public void testFetchUserApplicationsByUserContext_throwsException() {
        Mockito.doThrow(new RestClientException("blah")).when(restTemplate).postForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.any(Class.class));
        Set<MasheryUserApplication> oauthApps = null;
        try{
            oauthApps = classToTest.fetchUserApplicationsByUserContext("serviceKey", "userContext", TokenStatus.Active);
        } catch (RestClientException e) {
            Assert.assertTrue(e.getMessage().equals("blah"));
            Assert.assertTrue(oauthApps == null);
        }
        Mockito.verify(log).error(Mockito.anyString(), Mockito.any(Throwable.class));
    }

    @Test
    public void testFetchUserApplicationsByUserAccount(){
        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.any(Class.class))).thenReturn(wrappedMasheryUserApplication);
        Mockito.when(crmService.buildCrmHostName("testApp")).thenReturn("testApp@infusionsoft.com");
        Set<MasheryUserApplication> oauthApps = classToTest.fetchUserApplicationsByUserAccount(getUserAccount());
        Assert.assertTrue(oauthApps.iterator().next() != null);
        Assert.assertTrue(oauthApps.iterator().next().getName().equals(wrappedMasheryUserApplication.getResult().iterator().next().getName()));

    }

    @Test
    public void testRevokeAccessToken(){
        WrappedMasheryBoolean wrappedBooleanResult = new WrappedMasheryBoolean();
        wrappedBooleanResult.setResult(Boolean.TRUE);
        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.any(Class.class))).thenReturn(wrappedBooleanResult);
        Boolean wasSuccessful = classToTest.revokeAccessToken("serviceKey", "clientId", "accessToken");
        Assert.assertTrue(wasSuccessful);
    }

    @ObjectFactory
    public org.testng.IObjectFactory getObjectFactory() {
        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    private void createWrappedMasheryUserApplication(){
        wrappedMasheryUserApplication = new WrappedMasheryUserApplication();
        Set<MasheryUserApplication> userApps = new HashSet<MasheryUserApplication>();
        MasheryUserApplication app = new MasheryUserApplication();
        app.setName("ACME");
        app.setClient_id("client_id");
        Set<String> accessTokens = new HashSet<String>();
        accessTokens.add("token1");
        accessTokens.add("token2");
        accessTokens.add("token3");
        app.setAccess_tokens(accessTokens);

        userApps.add(app);

        wrappedMasheryUserApplication.setResult(userApps);
    }

    private UserAccount getUserAccount(){
        UserAccount ua = new UserAccount();
        ua.setAppName("testApp");
        User user = new User();
        user.setUsername("jojo@infusionsoft.com");
        ua.setUser(user);
        return ua;
    }
}
