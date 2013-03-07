package com.infusiontest.cas;

import com.infusionsoft.cas.services.InfusionsoftDataService;
import com.infusionsoft.cas.types.AppType;
import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import com.infusiontest.cas.testutils.TestingHibernateTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.testng.Assert;

/**
 * Test for most important stuff in the InfusionsoftDataService.
 */
public class InfusionsoftDataServiceTest {
    private HibernateTemplate hibernateTemplate;
    private InfusionsoftDataService infusionsoftDataService;

    @Before
    public void setUp() {
        hibernateTemplate = new TestingHibernateTemplate();

        infusionsoftDataService = new InfusionsoftDataService();
        infusionsoftDataService.setHibernateTemplate(hibernateTemplate);
    }

    @Test
    public void testAssociateAccountToUser() throws Exception {
        User user = createBobBarker();

        hibernateTemplate.save(user);
        infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");

        User userAgain = hibernateTemplate.get(User.class, user.getId());

        Assert.assertEquals(userAgain.getAccounts().size(), 1);
    }

    @Test
    public void testAssociateAccountAlreadyAssociated() throws Exception {
        User user = createBobBarker();

        hibernateTemplate.save(user);

        infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");
        infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");

        Assert.assertEquals(hibernateTemplate.get(User.class, user.getId()).getAccounts().size(), 1, "Bob should just have one mapped account");
    }

    @Test
    public void testAssociateAccountAlreadyAssociatedButDeactivated() throws Exception {
        User user = createBobBarker();
        UserAccount account;
        hibernateTemplate.save(user);

        account = infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");
        infusionsoftDataService.disableAccount(account);

        account = infusionsoftDataService.findUserAccount(user, "bb180", AppType.CRM, "bobbarker");
        Assert.assertTrue(account.isDisabled());

        infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");
        account = infusionsoftDataService.findUserAccount(user, "bb180", AppType.CRM, "bobbarker");
        Assert.assertFalse(account.isDisabled());
    }

    @Test
    public void testAssociateAccountSameNameDifferentType() throws Exception {
        User user = createBobBarker();

        hibernateTemplate.save(user);

        UserAccount account1 = infusionsoftDataService.associateAccountToUser(user, AppType.CRM, "bb180", "bobbarker");
        UserAccount account2 = infusionsoftDataService.associateAccountToUser(user, AppType.CUSTOMERHUB, "bb180", "bobbarker");

        Assert.assertTrue(account1.getId() != account2.getId());
    }

    @After
    public void tearDown() {
        hibernateTemplate.getSessionFactory().close();
    }

    private User createBobBarker() {
        User bob = new User();

        bob.setUsername("bob@priceisright.com");
        bob.setFirstName("Bob");
        bob.setLastName("Barker");
        bob.setEnabled(true);

        return bob;
    }
}
