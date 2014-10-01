package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.dto.OAuthAccessToken;
import com.infusionsoft.cas.oauth.dto.OAuthUserApplication;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.UserService;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.web.servlet.ModelAndView;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class OAuthControllerTest {
    private OAuthController classToTest;
    private UserService userService;
    private OAuthService oAuthService;

    private static final String serviceKey = "serviceKey";

    @BeforeTest
    public void beforeTest() {
        classToTest = new OAuthController();
        userService = Mockito.mock(UserService.class);
        oAuthService = Mockito.mock(OAuthService.class);

        Whitebox.setInternalState(classToTest, "userService", userService);
        Whitebox.setInternalState(classToTest, "oauthService", oAuthService);
        Whitebox.setInternalState(classToTest, "crmServiceKey", serviceKey);
    }

    @Test
    public void testManageAccounts() throws Exception {
        User user = new User();
        Mockito.when(userService.loadUser(1L)).thenReturn(user);
        UserAccount ua = new UserAccount();
        Mockito.when(userService.findUserAccount(user, 1L)).thenReturn(ua);
        Mockito.when(oAuthService.fetchUserApplicationsByUserAccount(serviceKey, ua)).thenReturn(createUserApplication());

        ModelAndView mv = classToTest.manageAccounts(1L, 1L);
        Assert.assertEquals(((Set<OAuthUserApplication>) mv.getModel().get("appsGrantedAccess")).size(), 1);
        Assert.assertEquals(mv.getModel().get("infusionsoftAccountId"), 1L);
    }

    private Set<OAuthUserApplication> createUserApplication() {
        Set<OAuthUserApplication> userApps = new HashSet<OAuthUserApplication>();

        Set<String> accessTokens = new HashSet<String>();
        accessTokens.add("token1");
        accessTokens.add("token2");
        accessTokens.add("token3");

        OAuthUserApplication app = new OAuthUserApplication("id", "ACME", "client_id", accessTokens);
//        app.setAccessTokens(accessTokens);

        userApps.add(app);
        return userApps;
    }


}
