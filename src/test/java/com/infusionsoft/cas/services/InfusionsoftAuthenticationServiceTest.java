package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.support.AppHelper;
import com.infusionsoft.cas.support.JsonHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.LinkedHashSet;

/**
 * Test for InfusionsoftAuthenticationService.
 */
public class InfusionsoftAuthenticationServiceTest {
    private InfusionsoftAuthenticationServiceImpl infusionsoftAuthenticationService;
    private JsonHelper jsonHelper;

    @BeforeTest
    public void setUp() {
        CrmService crmService = new CrmService();
        crmService.crmDomain = "infusionsoft.com";
        crmService.crmPort = 443;
        crmService.crmProtocol = "https";

        CustomerHubService customerHubService = new CustomerHubService();
        customerHubService.customerHubDomain = "customerhub.net";
        customerHubService.customerHubPort = 443;
        customerHubService.customerHubProtocol = "https";

        CommunityServiceImpl communityService = new CommunityServiceImpl();
        communityService.communityBaseUrl = "http://community.infusionsoft.com";

        AppHelper appHelper = new AppHelper();
        appHelper.crmService = crmService;
        appHelper.customerHubService = customerHubService;
        appHelper.communityService = communityService;

        jsonHelper = new JsonHelper();
        jsonHelper.appHelper = appHelper;


        infusionsoftAuthenticationService = new InfusionsoftAuthenticationServiceImpl();
        infusionsoftAuthenticationService.appHelper = appHelper;
        infusionsoftAuthenticationService.serverPrefix = "https://signin.infusionsoft.com";
        infusionsoftAuthenticationService.crmProtocol = "https";
        infusionsoftAuthenticationService.crmDomain = "infusionsoft.com";
        infusionsoftAuthenticationService.crmPort = "443";
        infusionsoftAuthenticationService.customerHubDomain = "customerhub.net";
        infusionsoftAuthenticationService.customerHubService = customerHubService;
        infusionsoftAuthenticationService.crmService = crmService;
        infusionsoftAuthenticationService.communityService = communityService;
        infusionsoftAuthenticationService.communityDomain = "community.infusionsoft.com";
    }

    @Test
    public void testJsonBuilding() {
        User user = new User();
        user.setId(13L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(true);
        user.setUsername("test.user@infusionsoft.com");

        UserAccount account1 = new UserAccount();
        account1.setAppName("app1");
        account1.setAppType(AppType.CRM);
        account1.setAppUsername("user1");
        account1.setAlias("My App #1");

        UserAccount account2 = new UserAccount();
        account2.setAppName("app2");
        account2.setAppType(AppType.CUSTOMERHUB);
        account2.setAppUsername("user2");
        account2.setAlias("My App #2");

        user.setAccounts(new LinkedHashSet<UserAccount>());
        user.getAccounts().add(account1);
        user.getAccounts().add(account2);

        String json = jsonHelper.buildUserInfoJSON(user);
        String expected = "{\"id\":13,\"lastName\":\"User\",\"username\":\"test.user@infusionsoft.com\",\"accounts\":[{\"appUrl\":\"https://app1.infusionsoft.com\",\"appName\":\"app1\",\"userName\":\"user1\",\"appAlias\":\"My App #1\",\"type\":\"crm\"},{\"appUrl\":\"https://app2.customerhub.net/admin\",\"appName\":\"app2\",\"userName\":\"user2\",\"appAlias\":\"My App #2\",\"type\":\"customerhub\"}],\"firstName\":\"Test\",\"displayName\":\"Test User\"}";

        Assert.assertEquals(json, expected, "JSON must match the expected format");
    }

    @Test
    public void testBuildAppUrl() {
        Assert.assertEquals(infusionsoftAuthenticationService.appHelper.buildAppUrl(AppType.CRM, "xy123"), "https://xy123.infusionsoft.com");
        Assert.assertEquals(infusionsoftAuthenticationService.appHelper.buildAppUrl(AppType.CUSTOMERHUB, "zz149"), "https://zz149.customerhub.net/admin");
        Assert.assertEquals(infusionsoftAuthenticationService.appHelper.buildAppUrl(AppType.COMMUNITY, "community"), "http://community.infusionsoft.com/caslogin.php");
    }

    @Test
    public void testGuessAppName() throws Exception {
        // CRM
        Assert.assertEquals("mm999", infusionsoftAuthenticationService.guessAppName(new URL("https://mm999.infusionsoft.com/gobbledygook")));
        Assert.assertEquals("something-long-and-weird", infusionsoftAuthenticationService.guessAppName(new URL("https://something-long-and-weird.infusionsoft.com/blah?foo=bar")));

        // CustomerHub
        Assert.assertEquals("xy231", infusionsoftAuthenticationService.guessAppName(new URL("https://xy231.customerhub.net/gobbledygook")));
        Assert.assertEquals("my-girlfriends-app", infusionsoftAuthenticationService.guessAppName(new URL("https://my-girlfriends-app.customerhub.net/blah?foo=bar")));

        // Community
        Assert.assertEquals("community", infusionsoftAuthenticationService.guessAppName(new URL("http://community.infusionsoft.com/gobbledygook?foo=bar")));

        // Unrecognized
        Assert.assertNull(infusionsoftAuthenticationService.guessAppName(new URL("http://www.google.com/search?q=lolcats")));

        // Our own
        Assert.assertNull(infusionsoftAuthenticationService.guessAppName(new URL("https://signin.infusionsoft.com/gobbledygook?foo=bar")));
    }

    @Test
    public void testGuessAppType() throws Exception {
        // CRM
        Assert.assertEquals(AppType.CRM, infusionsoftAuthenticationService.guessAppType(new URL("https://mm999.infusionsoft.com/gobbledygook")));
        Assert.assertEquals(AppType.CRM, infusionsoftAuthenticationService.guessAppType(new URL("https://something-long-and-weird.infusionsoft.com/blah?foo=bar")));

        // CustomerHub
        Assert.assertEquals(AppType.CUSTOMERHUB, infusionsoftAuthenticationService.guessAppType(new URL("https://xy231.customerhub.net/gobbledygook")));
        Assert.assertEquals(AppType.CUSTOMERHUB, infusionsoftAuthenticationService.guessAppType(new URL("https://my-girlfriends-app.customerhub.net/blah?foo=bar")));

        // Community
        Assert.assertEquals(AppType.COMMUNITY, infusionsoftAuthenticationService.guessAppType(new URL("http://community.infusionsoft.com/gobbledygook?foo=bar")));

        // Unrecognized
        Assert.assertNull(infusionsoftAuthenticationService.guessAppType(new URL("http://www.google.com/search?q=lolcats")));

        // Our own
        Assert.assertEquals(AppType.CAS, infusionsoftAuthenticationService.guessAppType(new URL("https://signin.infusionsoft.com/gobbledygook?foo=bar")));
    }
}
