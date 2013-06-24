package com.infusionsoft.cas.services;

import com.infusionsoft.cas.auth.LoginResult;
import com.infusionsoft.cas.dao.LoginAttemptDAO;
import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.LoginAttempt;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserPassword;
import com.infusionsoft.cas.support.AppHelper;
import org.mockito.ArgumentCaptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test for InfusionsoftAuthenticationService.
 */
public class InfusionsoftAuthenticationServiceTest {
    private InfusionsoftAuthenticationServiceImpl infusionsoftAuthenticationService;
    private User user;
    private UserPassword password;
    private static final String testUsername = "test.user@infusionsoft.com";
    private static final String testPassword = "passwordEncoded";
    private static final String testPasswordMD5 = "passwordEncodedMD5";
    private AppHelper appHelper;

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

        appHelper = new AppHelper();
        appHelper.crmService = crmService;
        appHelper.customerHubService = customerHubService;
        appHelper.communityService = communityService;

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
        infusionsoftAuthenticationService.userService = mock(UserService.class);
        infusionsoftAuthenticationService.passwordService = mock(PasswordService.class);
        infusionsoftAuthenticationService.loginAttemptDAO = mock(LoginAttemptDAO.class);

        user = new User();
        when(infusionsoftAuthenticationService.userService.loadUser(testUsername)).thenReturn(user);

        password = new UserPassword();
        password.setActive(true);
        password.setUser(user);
        password.setPasswordEncoded(testPassword);
        password.setPasswordEncodedMD5(testPasswordMD5);

        when(infusionsoftAuthenticationService.passwordService.passwordsMatch(any(UserPassword.class), anyString())).thenReturn(false);
        when(infusionsoftAuthenticationService.passwordService.passwordsMatch(password, testPassword)).thenReturn(true);
        when(infusionsoftAuthenticationService.passwordService.md5PasswordsMatch(any(UserPassword.class), anyString())).thenReturn(false);
        when(infusionsoftAuthenticationService.passwordService.md5PasswordsMatch(password, testPasswordMD5)).thenReturn(true);
    }

    @BeforeMethod
    public void setupForMethod() {
        user.setId(13L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(true);
        user.setUsername(testUsername);

        // TODO: use UTC date here
        password.setDateCreated(new Date());

        setupFailedLogins(0);
        when(infusionsoftAuthenticationService.passwordService.getPasswordForUser(user)).thenReturn(password);
        when(infusionsoftAuthenticationService.passwordService.isPasswordExpired(password)).thenReturn(false);
    }

    private List<LoginAttempt> setupFailedLogins(int failedCount) {
        List<LoginAttempt> loginAttemptList = new ArrayList<LoginAttempt>();
        for (int i = 0; i < failedCount; i++) {
            LoginAttempt loginAttempt = new LoginAttempt();
            loginAttempt.setSuccess(false);
            loginAttemptList.add(loginAttempt);
        }
        when(infusionsoftAuthenticationService.loginAttemptDAO.findByUsernameAndDateAttemptedGreaterThanOrderByDateAttemptedDesc(anyString(), any(Date.class))).thenReturn(loginAttemptList);
        return loginAttemptList;
    }

    @Test
    public void testBuildAppUrl() {
        // TODO: move this to a test class for AppHelper
        Assert.assertEquals(appHelper.buildAppUrl(AppType.CRM, "xy123"), "https://xy123.infusionsoft.com");
        Assert.assertEquals(appHelper.buildAppUrl(AppType.CUSTOMERHUB, "zz149"), "https://zz149.customerhub.net/admin");
        Assert.assertEquals(appHelper.buildAppUrl(AppType.COMMUNITY, "community"), "http://community.infusionsoft.com/caslogin.php");
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

        // Null
        Assert.assertNull(infusionsoftAuthenticationService.guessAppName((URL) null));
    }

    @Test
    public void testGuessAppNameString() throws Exception {
        // CRM
        Assert.assertEquals("mm999", infusionsoftAuthenticationService.guessAppName("https://mm999.infusionsoft.com/gobbledygook"));
        Assert.assertEquals("something-long-and-weird", infusionsoftAuthenticationService.guessAppName("https://something-long-and-weird.infusionsoft.com/blah?foo=bar"));

        // CustomerHub
        Assert.assertEquals("xy231", infusionsoftAuthenticationService.guessAppName("https://xy231.customerhub.net/gobbledygook"));
        Assert.assertEquals("my-girlfriends-app", infusionsoftAuthenticationService.guessAppName("https://my-girlfriends-app.customerhub.net/blah?foo=bar"));

        // Community
        Assert.assertEquals("community", infusionsoftAuthenticationService.guessAppName("http://community.infusionsoft.com/gobbledygook?foo=bar"));

        // Unrecognized
        Assert.assertNull(infusionsoftAuthenticationService.guessAppName("http://www.google.com/search?q=lolcats"));

        // Our own
        Assert.assertNull(infusionsoftAuthenticationService.guessAppName("https://signin.infusionsoft.com/gobbledygook?foo=bar"));

        // Null
        Assert.assertNull(infusionsoftAuthenticationService.guessAppName((String) null));
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

        // Null
        Assert.assertNull(infusionsoftAuthenticationService.guessAppType((URL) null));
    }

    @Test
    public void testGuessAppTypeString() throws Exception {
        // CRM
        Assert.assertEquals(AppType.CRM, infusionsoftAuthenticationService.guessAppType("https://mm999.infusionsoft.com/gobbledygook"));
        Assert.assertEquals(AppType.CRM, infusionsoftAuthenticationService.guessAppType("https://something-long-and-weird.infusionsoft.com/blah?foo=bar"));

        // CustomerHub
        Assert.assertEquals(AppType.CUSTOMERHUB, infusionsoftAuthenticationService.guessAppType("https://xy231.customerhub.net/gobbledygook"));
        Assert.assertEquals(AppType.CUSTOMERHUB, infusionsoftAuthenticationService.guessAppType("https://my-girlfriends-app.customerhub.net/blah?foo=bar"));

        // Community
        Assert.assertEquals(AppType.COMMUNITY, infusionsoftAuthenticationService.guessAppType("http://community.infusionsoft.com/gobbledygook?foo=bar"));

        // Unrecognized
        Assert.assertNull(infusionsoftAuthenticationService.guessAppType("http://www.google.com/search?q=lolcats"));

        // Our own
        Assert.assertEquals(AppType.CAS, infusionsoftAuthenticationService.guessAppType("https://signin.infusionsoft.com/gobbledygook?foo=bar"));

        // Null
        Assert.assertNull(infusionsoftAuthenticationService.guessAppType((String) null));
    }

    @Test
    public void testAttemptLoginAccountLocked() {
        // Account with some bad logins already, good password
        setupFailedLogins(3);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);

        // Account with some bad logins already, bad password
        setupFailedLogins(3);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 4);

        // Account on the verge of being locked, good password
        setupFailedLogins(5);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);

        // Account on the verge of being locked, bad password
        setupFailedLogins(5);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 6);

        // Account already locked, good password
        setupFailedLogins(6);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 7);

        // Account already locked, bad password
        setupFailedLogins(6);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 7);

        // Account already locked, forced unlocked by support
        List<LoginAttempt> loginAttemptList = setupFailedLogins(10); // Arbitrarily large number of failures
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setSuccess(true);
        loginAttemptList.add(0, loginAttempt);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginBadPassword() {
        // Bad password
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // Empty password
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, "");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // Null password
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, null);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // No password for user
        when(infusionsoftAuthenticationService.passwordService.getPasswordForUser(user)).thenReturn(null);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginDisabledUser() {
        user.setEnabled(false);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.DisabledUser);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginNoSuchUser() {
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin("noSuchUser@ever.com", "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.NoSuchUser);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginPasswordExpired() {
        when(infusionsoftAuthenticationService.passwordService.isPasswordExpired(password)).thenReturn(true);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.PasswordExpired);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginSuccess() {
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginWithMD5AccountLocked() {
        // Account with some bad logins already, good password
        setupFailedLogins(3);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);

        // Account with some bad logins already, bad password
        setupFailedLogins(3);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 4);

        // Account on the verge of being locked, good password
        setupFailedLogins(5);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);

        // Account on the verge of being locked, bad password
        setupFailedLogins(5);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 6);

        // Account already locked, good password
        setupFailedLogins(6);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 7);

        // Account already locked, bad password
        setupFailedLogins(6);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 7);

        // Account already locked, forced unlocked by support
        List<LoginAttempt> loginAttemptList = setupFailedLogins(10); // Arbitrarily large number of failures
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setSuccess(true);
        loginAttemptList.add(0, loginAttempt);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginWithMD5BadPassword() {
        // Bad password
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // Empty password
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, "");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // Null password
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, null);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // No password for user
        when(infusionsoftAuthenticationService.passwordService.getPasswordForUser(user)).thenReturn(null);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginWithMD5DisabledUser() {
        user.setEnabled(false);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.DisabledUser);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginWithMD5NoSuchUser() {
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password("noSuchUser@ever.com", "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.NoSuchUser);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginWithMD5PasswordExpired() {
        when(infusionsoftAuthenticationService.passwordService.isPasswordExpired(password)).thenReturn(true);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.PasswordExpired);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginWithMD5Success() {
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginResult.LoginStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testIsAccountLocked() throws Exception {
        setupFailedLogins(0);
        Assert.assertFalse(infusionsoftAuthenticationService.isAccountLocked(testUsername));

        setupFailedLogins(1);
        Assert.assertFalse(infusionsoftAuthenticationService.isAccountLocked(testUsername));

        setupFailedLogins(2);
        Assert.assertFalse(infusionsoftAuthenticationService.isAccountLocked(testUsername));

        setupFailedLogins(3);
        Assert.assertFalse(infusionsoftAuthenticationService.isAccountLocked(testUsername));

        setupFailedLogins(4);
        Assert.assertFalse(infusionsoftAuthenticationService.isAccountLocked(testUsername));

        setupFailedLogins(5);
        Assert.assertFalse(infusionsoftAuthenticationService.isAccountLocked(testUsername));

        setupFailedLogins(6);
        Assert.assertTrue(infusionsoftAuthenticationService.isAccountLocked(testUsername));

        setupFailedLogins(7);
        Assert.assertTrue(infusionsoftAuthenticationService.isAccountLocked(testUsername));

        setupFailedLogins(999);
        Assert.assertTrue(infusionsoftAuthenticationService.isAccountLocked(testUsername));
    }

    @Test
    public void testUnlockUser() throws Exception {
        reset(infusionsoftAuthenticationService.loginAttemptDAO);
        infusionsoftAuthenticationService.unlockUser(testUsername);
        ArgumentCaptor<LoginAttempt> argument = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(infusionsoftAuthenticationService.loginAttemptDAO, times(1)).save(argument.capture());
        Assert.assertEquals(argument.getValue().isSuccess(), true);
        Assert.assertEquals(argument.getValue().getUsername(), testUsername);
    }

    @Test(enabled = false)
    public void testVerifyAppCredentials() throws Exception {
        //infusionsoftAuthenticationService.verifyAppCredentials();
    }

    @Test(enabled = false)
    public void testGetCurrentUser() throws Exception {
        //infusionsoftAuthenticationService.getCurrentUser()
    }

    @Test(enabled = false)
    public void testHasCommunityAccount() throws Exception {
        //infusionsoftAuthenticationService.hasCommunityAccount()
    }

    @Test(enabled = false)
    public void testGetSupportPhoneNumber() throws Exception {
        //Assert.assertNotNull(infusionsoftAuthenticationService.getSupportPhoneNumber());
    }
}
