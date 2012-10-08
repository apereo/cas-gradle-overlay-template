package com.infusionsoft.cas.support;

import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.apache.commons.digester.ObjectParamRule;
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

    public final static String[] POSSIBLE_VALUES = {"accounts", "displayName", "firstName", "lastName", "email"};
    public final static Set<String> ATTRIBUTE_NAMES = new HashSet<String>(Arrays.asList(POSSIBLE_VALUES));

    public InfusionsoftAttributeDao() {
    }

    public InfusionsoftAttributeDao(Map<String, List<Object>> backingMap) {
        backingPerson = new AttributeNamedPersonImpl(backingMap);
    }

    public IPersonAttributes getPerson(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Illegal to invoke getPerson(String) with a null argument");
        }

        List<User> users = hibernateTemplate.find("from User u where u.username = ?", uid);
        Map<String, List<Object>> resultsMap = new HashMap<String, List<Object>>();

        if (users.size() > 0 ) {
            User currUser = users.get(0);
            List<UserAccount> accounts = hibernateTemplate.find("FROM UserAccount ua WHERE ua.user = ?", currUser);
            JSONObject rootObj = new JSONObject();
            JSONArray accountsArray = new JSONArray();

            for(UserAccount currAccount : accounts) {
                JSONObject accountToAdd = new JSONObject();

                accountToAdd.put("type", currAccount.getAppType());
                accountToAdd.put("appName", currAccount.getAppName());
                accountToAdd.put("userName", currAccount.getAppUsername());

                accountsArray.add(accountToAdd);
            }

            rootObj.put("accounts", accountsArray);

            resultsMap.put("accounts", Arrays.asList(new Object[] { rootObj }));
            resultsMap.put("displayName", Arrays.asList(new Object[] { currUser.getFirstName() + " " + currUser.getLastName() }));
            resultsMap.put("firstName", Arrays.asList(new Object[] { currUser.getFirstName() }));
            resultsMap.put("lastName", Arrays.asList(new Object[] { currUser.getLastName() }));
            resultsMap.put("email", Arrays.asList(new Object[] { currUser.getUsername() }));
        } else {
            logger.error("could not find a user record for: " + uid);
        }

        this.backingPerson = new AttributeNamedPersonImpl(resultsMap);
        return  this.backingPerson;
    }

    public Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(Map<String, List<Object>> query) {
        if (query == null) {
            throw new IllegalArgumentException("Illegal to invoke getPeople(Map) with a null argument");
        }

        if (backingPerson == null ) {
            return null;
        }

        return Collections.singleton(this.backingPerson);
    }

    public Set<String> getPossibleUserAttributeNames() {
        return ATTRIBUTE_NAMES;
    }

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
