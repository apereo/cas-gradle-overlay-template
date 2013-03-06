package com.infusiontest.cas;

import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.CustomerHubService;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.types.AppType;
import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

/**
 * Test for InfusionsoftAuthenticationService.
 */
public class InfusionsoftAuthenticationServiceTest {
    private CrmService crmService;
    private CustomerHubService customerHubService;
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Before
    public void setUp() {
        crmService = new CrmService();
        crmService.setCrmDomain("infusionsoft.com");
        crmService.setCrmPort(443);
        crmService.setCrmProtocol("https");

        customerHubService = new CustomerHubService();
        customerHubService.setCustomerHubDomain("customerhub.net");
        customerHubService.setCustomerHubPort(443);
        customerHubService.setCustomerHubProtocol("https");

        infusionsoftAuthenticationService = new InfusionsoftAuthenticationService();
        infusionsoftAuthenticationService.setCrmProtocol("https");
        infusionsoftAuthenticationService.setCrmDomain("infusionsoft.com");
        infusionsoftAuthenticationService.setCrmPort("443");
        infusionsoftAuthenticationService.setCustomerHubDomain("customerhub.net");
        infusionsoftAuthenticationService.setCustomerHubService(customerHubService);
        infusionsoftAuthenticationService.setCrmService(crmService);
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
        account2.setAppType(AppType.CRM);
        account2.setAppUsername("user2");
        account2.setAlias("My App #2");

        user.getAccounts().add(account1);
        user.getAccounts().add(account2);

        String json = infusionsoftAuthenticationService.buildUserInfoJSON(user);
        String expected = "{\"id\":13,\"lastName\":\"User\",\"username\":\"test.user@infusionsoft.com\",\"accounts\":[{\"appUrl\":\"https://app2.infusionsoft.com\",\"appName\":\"app2\",\"userName\":\"user2\",\"appAlias\":\"My App #2\",\"type\":\"crm\"},{\"appUrl\":\"https://app1.infusionsoft.com\",\"appName\":\"app1\",\"userName\":\"user1\",\"appAlias\":\"My App #1\",\"type\":\"crm\"}],\"firstName\":\"Test\",\"displayName\":\"Test User\"}";

        Assert.assertEquals(json, expected, "JSON must match the expected format");
    }
}
