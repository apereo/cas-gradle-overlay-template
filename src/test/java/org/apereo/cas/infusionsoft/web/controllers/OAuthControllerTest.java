package org.apereo.cas.infusionsoft.web.controllers;

import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.apereo.cas.infusionsoft.oauth.dto.OAuthUserApplication;
import org.apereo.cas.infusionsoft.oauth.services.OAuthService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.Set;

public class OAuthControllerTest {
    private OAuthController classToTest;
    private UserService userService;
    private OAuthService oAuthService;

    private static final String serviceKey = "serviceKey";

    @Before
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
