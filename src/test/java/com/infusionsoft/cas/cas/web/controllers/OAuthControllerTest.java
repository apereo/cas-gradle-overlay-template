package com.infusionsoft.cas.cas.web.controllers;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiClientService;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryUserApplication;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.UserService;
import com.infusionsoft.cas.web.controllers.OAuthController;
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
    private MasheryApiClientService masheryApiClientService;
    private OAuthService oAuthService;

    @BeforeTest
    public void beforeTest() {
        try{
            classToTest = new OAuthController();
        } catch(Exception e){

        }
        userService = Mockito.mock(UserService.class);
        masheryApiClientService = Mockito.mock(MasheryApiClientService.class);
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
        Mockito.when(oAuthService.fetchUserApplicationsByUserAccount(ua)).thenReturn(createMasheryUserApplication());

        ModelAndView mv = classToTest.manageAccounts(1L, 1L);
        Assert.assertEquals(((Set<MasheryUserApplication>) mv.getModel().get("appsGrantedAccess")).size(), 1);
        Assert.assertEquals(mv.getModel().get("infusionsoftAccountId"), 1L);

    }

    private Set<MasheryUserApplication> createMasheryUserApplication(){
        Set<MasheryUserApplication> userApps = new HashSet<MasheryUserApplication>();
        MasheryUserApplication app = new MasheryUserApplication();
        app.setName("ACME");
        app.setClient_id("client_id");
        Set<String> accessTokens = new HashSet<String>();
        accessTokens.add("token1");
        accessTokens.add("token2");
        accessTokens.add("token3");
        app.setAccess_tokens(accessTokens);

        userApps.add(app);
        return userApps;
    }


}
