//package com.infusionsoft.cas.services;
//
//import com.infusionsoft.cas.dao.UserDAO;
//import com.infusionsoft.cas.domain.AppType;
//import com.infusionsoft.cas.domain.User;
//import com.infusionsoft.cas.domain.UserAccount;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.testng.Assert;
//
///**
// * Test for most important stuff in the InfusionsoftDataService.
// */
//public class InfusionsoftDataServiceTest {
//    private InfusionsoftDataService infusionsoftDataService;
//
//    @Before
//    public void setUp() {
//        infusionsoftDataService = new InfusionsoftDataServiceImpl();
//        infusionsoftDataService.userDAO = new UserDAO();
//    }
//
//    @Test
//    public void testAssociateAccountToUser() throws Exception {
//        User user = createBobBarker();
//
//        infusionsoftDataService.userDAO.save(user);
//        infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");
//
//        User userAgain = infusionsoftDataService.userDAO.getById(user.getId());
//
//        Assert.assertEquals(userAgain.getAccounts().size(), 1);
//    }
//
//    @Test
//    public void testAssociateAccountAlreadyAssociated() throws Exception {
//        User user = createBobBarker();
//
//        infusionsoftDataService.userDAO.save(user);
//
//        infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");
//        infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");
//
//        Assert.assertEquals(infusionsoftDataService.userDAO.getById(user.getId()).getAccounts().size(), 1, "Bob should just have one mapped account");
//    }
//
//    @Test
//    public void testAssociateAccountAlreadyAssociatedButDeactivated() throws Exception {
//        User user = createBobBarker();
//        UserAccount account;
//        infusionsoftDataService.userDAO.save(user);
//
//        account = infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");
//        infusionsoftDataService.disableAccount(account);
//
//        account = infusionsoftDataService.findUserAccount(user, "bb180", AppType.CRM, "bobbarker");
//        Assert.assertTrue(account.isDisabled());
//
//        infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");
//        account = infusionsoftDataService.findUserAccount(user, "bb180", AppType.CRM, "bobbarker");
//        Assert.assertFalse(account.isDisabled());
//    }
//
//    @Test
//    public void testAssociateAccountSameNameDifferentType() throws Exception {
//        User user = createBobBarker();
//
//        infusionsoftDataService.userDAO.save(user);
//
//        UserAccount account1 = infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");
//        UserAccount account2 = infusionsoftDataService.associateAccountToUser(user, AppType.CUSTOMERHUB, "bb180", "bobbarker");
//
//        Assert.assertTrue(account1.getId().equals(account2.getId()));
//    }
//
//    @After
//    public void tearDown() {
////        hibernateTemplate.getSessionFactory().close();
//    }
//
//    private User createBobBarker() {
//        User bob = new User();
//
//        bob.setUsername("bob@priceisright.com");
//        bob.setFirstName("Bob");
//        bob.setLastName("Barker");
//        bob.setEnabled(true);
//
//        return bob;
//    }
//}
