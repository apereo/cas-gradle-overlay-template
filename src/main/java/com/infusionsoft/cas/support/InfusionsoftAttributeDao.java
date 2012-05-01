package com.infusionsoft.cas.support;

import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AbstractFlatteningPersonAttributeDao;
import org.jasig.services.persondir.support.AttributeNamedPersonImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.*;

public class InfusionsoftAttributeDao extends AbstractFlatteningPersonAttributeDao {

    private HibernateTemplate hibernateTemplate;
    private IPersonAttributes backingPerson = null;

    public final static String[] POSSIBLE_VALUES = {"accounts"};
    public final static Set<String> ATTRIBUTE_NAMES = new HashSet<String>(Arrays.asList(POSSIBLE_VALUES));

    public InfusionsoftAttributeDao() {
    }

    public InfusionsoftAttributeDao(Map<String, List<Object>> backingMap) {
        backingPerson = new AttributeNamedPersonImpl(backingMap);
    }

    @Override
    public IPersonAttributes getPerson(String uid) {
        System.out.println(" ******* getPerson() CALLED uid --> " + uid);
        if (uid == null) {
            throw new IllegalArgumentException("Illegal to invoke getPerson(String) with a null argument");
        }

        List<User> users = hibernateTemplate.find("from User u where u.username = ?", uid);

        Map<String, List<Object>> resultsMap = new HashMap<String, List<Object>>();

        //should pretty much always be 1
        if (users.size() > 0 ) {
            if (users.size() > 1){
                logger.warn("WARNING: for username: " + uid + " there appears to be more than 1 user being returned!");
            }
            //get the first one - again there should only be 1
            User currUser = users.get(0);
            List<UserAccount> accounts = hibernateTemplate.find("FROM UserAccount ua WHERE ua.user = ?", currUser);
            System.out.println(" ******* USER FOUND ID: " + currUser.getId());
            System.out.println(" ******* USER ACCOUNTS (from USER): " + currUser.getAccounts().size());
            System.out.println(" ******* USER ACCOUNTS (from Query): " + accounts.size());


            JSONObject rootObj = new JSONObject();
            JSONArray accountsList = new JSONArray();

            //For some reason currUser.getAccounts did not work
            for(UserAccount currAccount : accounts) {
                JSONObject accountToAdd = new JSONObject();
                accountToAdd.put("type", currAccount.getAppType());
                accountToAdd.put("appName", currAccount.getAppName());
                accountToAdd.put("userName", currAccount.getAppUsername());

                accountsList.add(accountToAdd);
            }

            rootObj.put("accounts", accountsList);

            List<Object> aList = new ArrayList<Object>();
            aList.add(rootObj);

            resultsMap.put("accounts", aList);

        } else {
            logger.error("ERROR! - could not find a user record for: " + uid);
        }

        this.backingPerson = new AttributeNamedPersonImpl(resultsMap);
        return  this.backingPerson;
    }

    @Override
    public Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(Map<String, List<Object>> query) {
        if (query == null) {
            throw new IllegalArgumentException("Illegal to invoke getPeople(Map) with a null argument");
        }
        if (this.backingPerson == null ) {
            return null;
        }
        return Collections.singleton(this.backingPerson);
    }

    @Override
    public Set<String> getPossibleUserAttributeNames() {
        return ATTRIBUTE_NAMES;
    }

    @Override
    public Set<String> getAvailableQueryAttributes() {
        return null;
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public Map<String, List<Object>> getBackingMap() {
        return this.backingPerson.getAttributes();
    }

    public void setBackingMap(final Map<String, List<Object>> backingMap) {
        this.backingPerson = new AttributeNamedPersonImpl(backingMap);
    }

}
