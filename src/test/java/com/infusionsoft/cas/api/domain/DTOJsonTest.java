package com.infusionsoft.cas.api.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.Authority;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.services.CommunityServiceImpl;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.CustomerHubService;
import com.infusionsoft.cas.support.AppHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Test for serializing DTOs to JSON.
 */
public class DTOJsonTest {
    private AppHelper appHelper;
    private User user;
    private UserAccount account1;

    @BeforeTest
    public void setUp() {
        CrmService crmService = new CrmService();
        crmService.setCrmDomain("infusionsoft.com");
        crmService.setCrmPort(443);
        crmService.setCrmProtocol("https");

        CustomerHubService customerHubService = new CustomerHubService();
        customerHubService.setCustomerHubDomain("customerhub.net");
        customerHubService.setCustomerHubPort(443);
        customerHubService.setCustomerHubProtocol("https");

        CommunityServiceImpl communityService = new CommunityServiceImpl();
        communityService.setCommunityBaseUrl("http://community.infusionsoft.com");

        appHelper = new AppHelper();
        appHelper.crmService = crmService;
        appHelper.customerHubService = customerHubService;
        appHelper.communityService = communityService;
    }

    @BeforeMethod
    public void setupForMethod() {
        user = new User();
        user.setId(13L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(true);
        user.setUsername("test.user@infusionsoft.com");

        account1 = new UserAccount();
        account1.setAppName("app1");
        account1.setAppType(AppType.CRM);
        account1.setAppUsername("user1");
        account1.setAlias("My App #1");
        account1.setUser(user);

        UserAccount account2 = new UserAccount();
        account2.setAppName("app2");
        account2.setAppType(AppType.CUSTOMERHUB);
        account2.setAppUsername("user2");
        account2.setAlias("My App #2");
        account2.setUser(user);

        user.setAccounts(new LinkedHashSet<UserAccount>());
        user.getAccounts().add(account1);
        user.getAccounts().add(account2);

        Authority authority1 = new Authority();
        authority1.setAuthority("AUTHORITY1");
        authority1.setId(1L);
        authority1.setUsers(new LinkedHashSet<User>(Arrays.asList(new User[]{user})));

        Authority authority2 = new Authority();
        authority2.setAuthority("CAS_ROLE_USER");
        authority2.setId(1L);
        authority2.setUsers(new LinkedHashSet<User>(Arrays.asList(new User[]{user})));

        user.setAuthorities(new LinkedHashSet<Authority>());
        user.getAuthorities().add(authority1);
        user.getAuthorities().add(authority2);
    }

    @Test
    public void testUserDTOJson() throws Exception {
        List<UserAccount> accounts = new ArrayList<UserAccount>(user.getAccounts());
        UserDTO userDTO = new UserDTO(user, accounts, appHelper);

        String actualJson = serializeToJson(userDTO);
        String expectedJson = "{\"globalUserId\":13,\"username\":\"test.user@infusionsoft.com\",\"displayName\":\"Test User\",\"firstName\":\"Test\",\"lastName\":\"User\",\"linkedApps\":[{\"appType\":\"CRM\",\"appName\":\"app1\",\"appUsername\":\"user1\",\"appAlias\":\"My App #1\",\"appUrl\":\"https://app1.infusionsoft.com\"},{\"appType\":\"CUSTOMERHUB\",\"appName\":\"app2\",\"appUsername\":\"user2\",\"appAlias\":\"My App #2\",\"appUrl\":\"https://app2.customerhub.net/admin\"}],\"authorities\":[\"AUTHORITY1\",\"CAS_ROLE_USER\"],\"casGlobalId\":13}";
        Assert.assertEquals(actualJson, expectedJson, "JSON must match the expected format");
    }

    @Test
    public void testNestedUserAccountDTOJson() throws Exception {
        UserAccountDTO userAccountDTO = new UserAccountDTO(account1, appHelper);
        String actualJson = serializeToJson(userAccountDTO);
        String expectedJson = "{\"appType\":\"CRM\",\"appName\":\"app1\",\"appUsername\":\"user1\",\"appAlias\":\"My App #1\",\"appUrl\":\"https://app1.infusionsoft.com\"}";
        Assert.assertEquals(actualJson, expectedJson, "JSON must match the expected format");
    }

    @Test
    public void testUserAccountDTOJson() throws Exception {
        AccountDTO accountDTO = new AccountDTO(account1, appHelper);
        String actualJson = serializeToJson(accountDTO);
        String expectedJson = "{\"appType\":\"CRM\",\"appName\":\"app1\",\"appUsername\":\"user1\",\"appAlias\":\"My App #1\",\"appUrl\":\"https://app1.infusionsoft.com\",\"infusionsoftId\":\"test.user@infusionsoft.com\",\"globalUserId\":13,\"casGlobalId\":13}";
        Assert.assertEquals(actualJson, expectedJson, "JSON must match the expected format");
    }

    @Test
    public void testAPIErrorDTOWithoutRelatedObjectJson() throws Exception {
        APIErrorDTO apiErrorDTO = new APIErrorDTO("code", "message");
        String actualJson = serializeToJson(apiErrorDTO);
        String expectedJson = "{\"code\":\"code\",\"message\":\"message\"}";
        Assert.assertEquals(actualJson, expectedJson, "JSON must match the expected format");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAPIErrorDTOWithStringRelatedObjectJson() throws Exception {
        APIErrorDTO apiErrorDTO = new APIErrorDTO("code", "message", "relatedObject");
        String actualJson = serializeToJson(apiErrorDTO);
        String expectedJson = "{\"code\":\"code\",\"message\":\"message\",\"relatedObject\":\"relatedObject\"}";
        Assert.assertEquals(actualJson, expectedJson, "JSON must match the expected format");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAPIErrorDTOWithComplexRelatedObjectJson() throws Exception {
        APIErrorDTO apiErrorDTO = new APIErrorDTO("code", "message", new AccountDTO[]{new AccountDTO(account1, appHelper)});
        String actualJson = serializeToJson(apiErrorDTO);
        String expectedJson = "{\"code\":\"code\",\"message\":\"message\",\"relatedObject\":[\"Account[]\",[{\"appType\":\"CRM\",\"appName\":\"app1\",\"appUsername\":\"user1\",\"appAlias\":\"My App #1\",\"appUrl\":\"https://app1.infusionsoft.com\",\"infusionsoftId\":\"test.user@infusionsoft.com\",\"globalUserId\":13,\"casGlobalId\":13}]]}";
        Assert.assertEquals(actualJson, expectedJson, "JSON must match the expected format");
    }

    private String serializeToJson(Object objectToSerialize) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream, objectToSerialize);
        return outputStream.toString();
    }
}
