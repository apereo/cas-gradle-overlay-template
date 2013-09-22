package com.infusionsoft.cas.cas.web.controllers;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.MasheryService;
import com.infusionsoft.cas.oauth.domain.MasheryUserApplication;
import com.infusionsoft.cas.oauth.wrappers.WrappedMasheryUserApplication;
import com.infusionsoft.cas.services.UserService;
import com.infusionsoft.cas.web.controllers.CentralController;
import junit.framework.Assert;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class CentralControllerTest {
    private CentralController classToTest;
    private UserService userService;
    private MasheryService masheryService;

    @BeforeTest
    public void beforeTest() {
        try{
            classToTest = new CentralController();
        } catch(Exception e){

        }
        userService = Mockito.mock(UserService.class);
        masheryService = Mockito.mock(MasheryService.class);

        Whitebox.setInternalState(classToTest, "userService", userService);
        Whitebox.setInternalState(classToTest, "masheryService", masheryService);
    }

    @Test
    public void testManageAccounts() throws Exception {
        User user = new User();
        Mockito.when(userService.loadUser(1L)).thenReturn(user);
        UserAccount ua = new UserAccount();
        Mockito.when(userService.findUserAccount(user, 1L)).thenReturn(ua);
        Mockito.when(masheryService.fetchUserApplicationsByUserAccount(ua)).thenReturn(createMasheryUserApplication());

        ModelAndView mv = classToTest.manageAccounts(1L, 1L);
        Assert.assertTrue(((Set<MasheryUserApplication>)mv.getModel().get("appsGrantedAccess")).size() ==1);
        Assert.assertTrue(((Long)mv.getModel().get("infusionsoftAccountId")).equals(1L) );

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
