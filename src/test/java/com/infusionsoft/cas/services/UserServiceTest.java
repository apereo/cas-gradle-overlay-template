package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.*;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.oauth.services.OAuthService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserServiceImpl serviceToTest;

    @Mock
    private AuthorityDAO authorityDAO;
    @Mock
    private LoginAttemptDAO loginAttemptDAO;
    @Mock
    private MailService mailService;
    @Mock
    private PasswordService passwordService;
    @Mock
    private PendingUserAccountDAO pendingUserAccountDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private UserAccountDAO userAccountDAO;
    @Mock
    private OAuthService oauthService;

    private User user;
    private static final String testUsername = "test.user@infusionsoft.com";
    private static final String testFirstName = "Test";
    private static final String testLastName = "User";

    @Before
    public void setupForMethod() {
        user = new User();
        user.setId(13L);
        user.setFirstName(testFirstName);
        user.setLastName(testLastName);
        user.setEnabled(true);
        user.setUsername(testUsername);

        MockitoAnnotations.initMocks(this);

        serviceToTest = new UserServiceImpl();
        Whitebox.setInternalState(serviceToTest, "authorityDAO", authorityDAO);
        Whitebox.setInternalState(serviceToTest, "loginAttemptDAO", loginAttemptDAO);
        Whitebox.setInternalState(serviceToTest, "mailService", mailService);
        Whitebox.setInternalState(serviceToTest, "passwordService", passwordService);
        Whitebox.setInternalState(serviceToTest, "pendingUserAccountDAO", pendingUserAccountDAO);
        Whitebox.setInternalState(serviceToTest, "userDAO", userDAO);
        Whitebox.setInternalState(serviceToTest, "userAccountDAO", userAccountDAO);

        when(userDAO.findOne(user.getId())).thenReturn(user);
        when(userDAO.findByUsername(testUsername)).thenReturn(user);
        when(userDAO.findByUsernameAndEnabled(testUsername, true)).thenReturn(user);
        when(userDAO.save(user)).thenReturn(user);
    }

    // TODO: a bunch of other functions in UserService still have no testing at all

    @Test
    public void testUpdatePasswordRecoveryCode() throws Exception {
        final String oldPasswordRecoveryCode = "Not null";
        final DateTime oldPasswordRecoveryCodeCreatedTime = new DateTime(1);
        user.setPasswordRecoveryCode(oldPasswordRecoveryCode);
        user.setPasswordRecoveryCodeCreatedTime(oldPasswordRecoveryCodeCreatedTime);

        User returnedUser = serviceToTest.updatePasswordRecoveryCode(user.getId());

        // Make sure save was called and with the right user
        verify(userDAO, times(1)).save(returnedUser);
        Assert.assertSame(returnedUser, user);
        // Make sure the recovery code value and date were updated
        Assert.assertFalse(StringUtils.equals(returnedUser.getPasswordRecoveryCode(), oldPasswordRecoveryCode));
        Assert.assertFalse(returnedUser.getPasswordRecoveryCodeCreatedTime() == oldPasswordRecoveryCodeCreatedTime);
        // Nothing else should have changed
        Assert.assertEquals(returnedUser.getUsername(), testUsername);
        Assert.assertEquals(returnedUser.getFirstName(), testFirstName);
        Assert.assertEquals(returnedUser.getLastName(), testLastName);
        Assert.assertTrue(returnedUser.isEnabled());
    }

    @Test
    public void testClearPasswordRecoveryCode() throws Exception {
        user.setPasswordRecoveryCode("Not null");
        user.setPasswordRecoveryCodeCreatedTime(new DateTime());

        User returnedUser = serviceToTest.clearPasswordRecoveryCode(user.getId());

        // Make sure save was called and with the right user
        verify(userDAO, times(1)).save(returnedUser);
        Assert.assertSame(returnedUser, user);
        // Make sure the recovery code value and date were cleared out
        Assert.assertNull(returnedUser.getPasswordRecoveryCode());
        Assert.assertNull(returnedUser.getPasswordRecoveryCodeCreatedTime());
        // Nothing else should have changed
        Assert.assertEquals(returnedUser.getUsername(), testUsername);
        Assert.assertEquals(returnedUser.getFirstName(), testFirstName);
        Assert.assertEquals(returnedUser.getLastName(), testLastName);
        Assert.assertTrue(returnedUser.isEnabled());
    }
}
