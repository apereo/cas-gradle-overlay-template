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
//    private MasheryApiClientService masheryApiClientService;
    private OAuthService oAuthService;

    @BeforeTest
    public void beforeTest() {
//        try{
            classToTest = new OAuthController();
//        } catch(Exception e){

//        }
        userService = Mockito.mock(UserService.class);
//        masheryApiClientService = Mockito.mock(MasheryApiClientService.class);
        oAuthService =  Mockito.mock(OAuthService.class);

        Whitebox.setInternalState(classToTest, "userService", userService);
        Whitebox.setInternalState(classToTest, "oauthService", oAuthService);
    }

    @Test
    public void testManageAccounts() throws Exception {
        User user = new User();
        Mockito.when(userService.loadUser(1L)).thenReturn(user);
        UserAccount ua = new UserAccount();
        Mockito.when(userService.findUserAccount(user, 1L)).thenReturn(ua);
        Mockito.when(oAuthService.fetchUserApplicationsByUserAccount(ua)).thenReturn(createUserApplication());

        ModelAndView mv = classToTest.manageAccounts(1L, 1L);
        Assert.assertEquals(((Set<OAuthUserApplication>) mv.getModel().get("appsGrantedAccess")).size(), 1);
        Assert.assertEquals(mv.getModel().get("infusionsoftAccountId"), 1L);
    }

    private Set<OAuthUserApplication> createUserApplication(){
        Set<OAuthUserApplication> userApps = new HashSet<OAuthUserApplication>();

        Set<OAuthAccessToken> accessTokens = new HashSet<OAuthAccessToken>();
        accessTokens.add(new OAuthAccessToken("token1", null, null, null, null));
        accessTokens.add(new OAuthAccessToken("token2", null, null, null, null));
        accessTokens.add(new OAuthAccessToken("token3", null, null, null, null));

        OAuthUserApplication app = new OAuthUserApplication("id", "ACME", "client_id", accessTokens);
        app.setAccessTokens(accessTokens);

        userApps.add(app);
        return userApps;
    }


}
