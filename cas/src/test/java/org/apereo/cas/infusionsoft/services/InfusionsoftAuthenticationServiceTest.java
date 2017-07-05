package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.core.CasServerProperties;
import org.apereo.cas.infusionsoft.authentication.LoginResult;
import org.apereo.cas.infusionsoft.config.properties.HostConfigurationProperties;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.LoginAttemptDAO;
import org.apereo.cas.infusionsoft.domain.*;
import org.apereo.cas.infusionsoft.support.UserAccountTransformer;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    private UserPassword newPassword;
    private static final String testUsername = "test.user@infusionsoft.com";
    private static final String testPassword = "passwordEncoded";
    private static final String testPasswordMD5 = "passwordEncodedMD5";
    private static final String testNewPassword = "newPasswordEncoded";
    private static final String testNewPasswordMD5 = "newPasswordEncodedMD5";
    private UserAccountTransformer userAccountTransformer;

    @Mock
    private TicketRegistry ticketRegistry;

    @Mock
    private LoginAttemptDAO loginAttemptDAO;

    @Mock
    private UserService userService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    @Before
    public void setupForMethod() {
        HostConfigurationProperties communityConfig = new HostConfigurationProperties();
        communityConfig.setDomain("community.infusionsoft.com");
        communityConfig.setPort(443);
        communityConfig.setProtocol("https");

        HostConfigurationProperties crmConfig = new HostConfigurationProperties();
        crmConfig.setDomain("infusionsoft.com");
        crmConfig.setPort(443);
        crmConfig.setProtocol("https");

        HostConfigurationProperties customerHubConfig = new HostConfigurationProperties();
        customerHubConfig.setDomain("customerhub.net");
        customerHubConfig.setPort(443);
        customerHubConfig.setProtocol("https");

        HostConfigurationProperties marketplaceConfig = new HostConfigurationProperties();
        marketplaceConfig.setDomain("marketplace.infusionsoft.com");
        marketplaceConfig.setPort(443);
        marketplaceConfig.setProtocol("https");

        InfusionsoftConfigurationProperties infusionsoftConfigurationProperties = new InfusionsoftConfigurationProperties();
        infusionsoftConfigurationProperties.setCommunity(communityConfig);
        infusionsoftConfigurationProperties.setCrm(crmConfig);
        infusionsoftConfigurationProperties.setCustomerhub(customerHubConfig);
        infusionsoftConfigurationProperties.setMarketplace(marketplaceConfig);

        CasServerProperties casServerProperties = new CasServerProperties();
        casServerProperties.setPrefix("https://signin.infusionsoft.com");

        CasConfigurationProperties casProperties = new CasConfigurationProperties();
        casProperties.setServer(casServerProperties);

        userAccountTransformer = new UserAccountTransformer(infusionsoftConfigurationProperties);

        MockitoAnnotations.initMocks(this);

        infusionsoftAuthenticationService = new InfusionsoftAuthenticationServiceImpl(loginAttemptDAO, userService, passwordService);

        user = new User();
        when(userService.loadUser(testUsername)).thenReturn(user);

        password = new UserPassword();
        password.setUser(user);
        password.setPasswordEncoded(testPassword);
        password.setPasswordEncodedMD5(testPasswordMD5);

        newPassword = new UserPassword();
        newPassword.setUser(user);
        newPassword.setPasswordEncoded(testNewPassword);
        newPassword.setPasswordEncodedMD5(testNewPasswordMD5);

        when(passwordService.getMatchingPasswordForUser(any(User.class), anyString())).thenReturn(null);
        when(passwordService.getMatchingMD5PasswordForUser(any(User.class), anyString())).thenReturn(null);

        user.setId(13L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(true);
        user.setUsername(testUsername);

        // TODO: use UTC date here
        password.setDateCreated(new Date());

        setupFailedLogins(0);
        when(passwordService.getMatchingPasswordForUser(user, testPassword)).thenReturn(password);
        when(passwordService.getMatchingMD5PasswordForUser(user, testPasswordMD5)).thenReturn(password);
        when(passwordService.getMatchingPasswordForUser(user, testNewPassword)).thenReturn(newPassword);
        when(passwordService.getMatchingMD5PasswordForUser(user, testNewPasswordMD5)).thenReturn(newPassword);
        when(passwordService.getLatestPassword(user)).thenReturn(password);
        when(passwordService.isPasswordExpired(password)).thenReturn(false);
    }

    private List<LoginAttempt> setupFailedLogins(int failedCount) {
        List<LoginAttempt> loginAttemptList = new ArrayList<LoginAttempt>();
        for (int i = 0; i < failedCount; i++) {
            // Setup failed logins of all types
            LoginAttempt loginAttempt = new LoginAttempt();
            int modulusOfI = i % 4;
            if (modulusOfI == 0) {
                loginAttempt.setStatus(LoginAttemptStatus.AccountLocked);
            } else if (modulusOfI == 1) {
                loginAttempt.setStatus(LoginAttemptStatus.BadPassword);
            } else if (modulusOfI == 2) {
                loginAttempt.setStatus(LoginAttemptStatus.DisabledUser);
            } else {
                loginAttempt.setStatus(LoginAttemptStatus.NoSuchUser);
            }
            loginAttemptList.add(loginAttempt);

            // Also add a OldPassword result to ensure they are being ignored
            loginAttempt = new LoginAttempt();
            loginAttempt.setStatus(LoginAttemptStatus.OldPassword);
            loginAttemptList.add(loginAttempt);
        }
        when(loginAttemptDAO.findByUsernameAndDateAttemptedGreaterThanOrderByDateAttemptedDesc(anyString(), any(Date.class))).thenReturn(loginAttemptList);
        return loginAttemptList;
    }

    @Test
    public void testBuildAppUrl() {
        // TODO: move this to a test class for UserAccountTransformer
        Assert.assertEquals(userAccountTransformer.buildAppUrl(AppType.CRM, "xy123"), "https://xy123.infusionsoft.com");
        Assert.assertEquals(userAccountTransformer.buildAppUrl(AppType.CUSTOMERHUB, "zz149"), "https://zz149.customerhub.net/admin");
        Assert.assertEquals(userAccountTransformer.buildAppUrl(AppType.COMMUNITY, "community"), "https://community.infusionsoft.com");
        Assert.assertEquals(userAccountTransformer.buildAppUrl(AppType.MARKETPLACE, "marketplace"), "https://marketplace.infusionsoft.com");
    }

    @Test
    public void testAttemptLoginAccountLocked() {
        // Account with some bad logins already, good password
        setupFailedLogins(3);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);

        // Account with some bad logins already, bad password
        setupFailedLogins(3);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 4);

        // Account on the verge of being locked, good password
        setupFailedLogins(5);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);

        // Account on the verge of being locked, bad password
        setupFailedLogins(5);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 6);

        // Account already locked, good password
        setupFailedLogins(6);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 7);

        // Account already locked, bad password
        setupFailedLogins(6);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 7);

        // Account already locked, forced unlocked by support
        List<LoginAttempt> loginAttemptList = setupFailedLogins(10); // Arbitrarily large number of failures
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setStatus(LoginAttemptStatus.UnlockedByAdmin);
        loginAttemptList.add(0, loginAttempt);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginBadPassword() {
        // Bad password
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // Empty password
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, "");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // Null password
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, null);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // No password for user
        when(passwordService.getMatchingPasswordForUser(user, testPassword)).thenReturn(null);
        loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginDisabledUser() {
        user.setEnabled(false);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.DisabledUser);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginNoSuchUser() {
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin("noSuchUser@ever.com", "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.NoSuchUser);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginPasswordExpired() {
        when(passwordService.isPasswordExpired(password)).thenReturn(true);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.PasswordExpired);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginSuccess() {
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginOldPassword() {
        when(passwordService.getLatestPassword(user)).thenReturn(newPassword);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLogin(testUsername, testPassword);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.OldPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginWithMD5AccountLocked() {
        // Account with some bad logins already, good password
        setupFailedLogins(3);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);

        // Account with some bad logins already, bad password
        setupFailedLogins(3);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 4);

        // Account on the verge of being locked, good password
        setupFailedLogins(5);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);

        // Account on the verge of being locked, bad password
        setupFailedLogins(5);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 6);

        // Account already locked, good password
        setupFailedLogins(6);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 7);

        // Account already locked, bad password
        setupFailedLogins(6);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.AccountLocked);
        Assert.assertEquals(loginResult.getFailedAttempts(), 7);

        // Account already locked, forced unlocked by support
        List<LoginAttempt> loginAttemptList = setupFailedLogins(10); // Arbitrarily large number of failures
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setStatus(LoginAttemptStatus.UnlockedByAdmin);
        loginAttemptList.add(0, loginAttempt);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginWithMD5BadPassword() {
        // Bad password
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // Empty password
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, "");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // Null password
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, null);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);

        // No password for user
        when(passwordService.getMatchingMD5PasswordForUser(user, testPasswordMD5)).thenReturn(null);
        loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.BadPassword);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginWithMD5DisabledUser() {
        user.setEnabled(false);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.DisabledUser);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginWithMD5NoSuchUser() {
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password("noSuchUser@ever.com", "badPassword");
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.NoSuchUser);
        Assert.assertEquals(loginResult.getFailedAttempts(), 1);
    }

    @Test
    public void testAttemptLoginWithMD5PasswordExpired() {
        when(passwordService.isPasswordExpired(password)).thenReturn(true);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.PasswordExpired);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginWithMD5Success() {
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.Success);
        Assert.assertEquals(loginResult.getFailedAttempts(), 0);
    }

    @Test
    public void testAttemptLoginWithMD5OldPassword() {
        when(passwordService.getLatestPassword(user)).thenReturn(newPassword);
        LoginResult loginResult = infusionsoftAuthenticationService.attemptLoginWithMD5Password(testUsername, testPasswordMD5);
        Assert.assertEquals(loginResult.getLoginStatus(), LoginAttemptStatus.OldPassword);
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
    public void testCompletePasswordReset() throws Exception {
        reset(userService);
        reset(loginAttemptDAO);
        infusionsoftAuthenticationService.completePasswordReset(user);

        ArgumentCaptor<Long> userIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userService, times(1)).clearPasswordRecoveryCode(userIdArgumentCaptor.capture());
        Assert.assertEquals(userIdArgumentCaptor.getValue(), user.getId());

        ArgumentCaptor<LoginAttempt> loginAttemptArgumentCaptor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(loginAttemptDAO, times(1)).save(loginAttemptArgumentCaptor.capture());
        Assert.assertEquals(loginAttemptArgumentCaptor.getValue().getStatus(), LoginAttemptStatus.PasswordReset);
        Assert.assertEquals(loginAttemptArgumentCaptor.getValue().getUsername(), testUsername);
    }
}
