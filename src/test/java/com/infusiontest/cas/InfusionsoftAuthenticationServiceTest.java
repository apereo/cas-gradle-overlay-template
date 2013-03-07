package com.infusiontest.cas;

import com.infusionsoft.cas.services.CommunityService;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.CustomerHubService;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.types.*;
import com.infusiontest.cas.testutils.TestingHibernateTemplate;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.testng.Assert;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.TreeSet;

/**
 * Test for InfusionsoftAuthenticationService.
 */
public class InfusionsoftAuthenticationServiceTest {
    private HibernateTemplate hibernateTemplate;
    private CrmService crmService;
    private CustomerHubService customerHubService;
    private CommunityService communityService;
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Before
    public void setUp() {
        hibernateTemplate = new TestingHibernateTemplate();

        crmService = new CrmService();
        crmService.setCrmDomain("infusionsoft.com");
        crmService.setCrmPort(443);
        crmService.setCrmProtocol("https");

        customerHubService = new CustomerHubService();
        customerHubService.setCustomerHubDomain("customerhub.net");
        customerHubService.setCustomerHubPort(443);
        customerHubService.setCustomerHubProtocol("https");

        communityService = new CommunityService();
        communityService.setCommunityBaseUrl("http://community.infusionsoft.com");

        infusionsoftAuthenticationService = new InfusionsoftAuthenticationService();
        infusionsoftAuthenticationService.setServerPrefix("https://signin.infusionsoft.com");
        infusionsoftAuthenticationService.setCrmProtocol("https");
        infusionsoftAuthenticationService.setCrmDomain("infusionsoft.com");
        infusionsoftAuthenticationService.setCrmPort("443");
        infusionsoftAuthenticationService.setCustomerHubDomain("customerhub.net");
        infusionsoftAuthenticationService.setCustomerHubService(customerHubService);
        infusionsoftAuthenticationService.setCrmService(crmService);
        infusionsoftAuthenticationService.setCommunityService(communityService);
        infusionsoftAuthenticationService.setCommunityDomain("community.infusionsoft.com");
        infusionsoftAuthenticationService.setHibernateTemplate(hibernateTemplate);
    }

    @Test
    public void testJsonBuilding() {
        User user = new User();
        user.setId(new Long(13));
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

        String json = infusionsoftAuthenticationService.buildUserInfoJSON(user);
        String expected = "{\"id\":13,\"lastName\":\"User\",\"username\":\"test.user@infusionsoft.com\",\"accounts\":[{\"appUrl\":\"https://app1.infusionsoft.com\",\"appName\":\"app1\",\"userName\":\"user1\",\"appAlias\":\"My App #1\",\"type\":\"crm\"},{\"appUrl\":\"https://app2.customerhub.net/admin\",\"appName\":\"app2\",\"userName\":\"user2\",\"appAlias\":\"My App #2\",\"type\":\"customerhub\"}],\"firstName\":\"Test\",\"displayName\":\"Test User\"}";

        Assert.assertEquals(json, expected, "JSON must match the expected format");
    }

    @Test
    public void testBuildAppUrl() {
        Assert.assertEquals(infusionsoftAuthenticationService.buildAppUrl(AppType.CRM, "xy123"),  "https://xy123.infusionsoft.com");
        Assert.assertEquals(infusionsoftAuthenticationService.buildAppUrl(AppType.CUSTOMERHUB, "zz149"), "https://zz149.customerhub.net/admin");
        Assert.assertEquals(infusionsoftAuthenticationService.buildAppUrl(AppType.COMMUNITY, "community"), "http://community.infusionsoft.com/caslogin.php");
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
