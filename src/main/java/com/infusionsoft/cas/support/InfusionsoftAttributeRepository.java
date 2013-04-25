package com.infusionsoft.cas.support;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.services.UserService;
import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AbstractFlatteningPersonAttributeDao;
import org.jasig.services.persondir.support.AttributeNamedPersonImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Special class for adding custom Infusionsoft attributes to the CAS/SAML response. This is how downstream applications
 * know if users are mapped to a local account, among other things.
 */
@Component("attributeRepository")
public class InfusionsoftAttributeRepository extends AbstractFlatteningPersonAttributeDao {

    @Autowired
    UserService userService;

    @Autowired
    JsonHelper jsonHelper;

    private IPersonAttributes backingPerson = null;

    public final static String[] POSSIBLE_VALUES = {"accounts", "displayName", "firstName", "lastName", "email"};
    public final static Set<String> ATTRIBUTE_NAMES = new HashSet<String>(Arrays.asList(POSSIBLE_VALUES));

    public InfusionsoftAttributeRepository() {
    }

    public InfusionsoftAttributeRepository(Map<String, List<Object>> backingMap) {
        backingPerson = new AttributeNamedPersonImpl(backingMap);
    }

    /**
     * Resolves a user and sets custom attributes.
     */
    @SuppressWarnings(value = "unchecked")
    public IPersonAttributes getPerson(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("Illegal to invoke getPerson(String) with a null argument");
        }

        User user = userService.loadUser(uid);
        Map<String, List<Object>> resultsMap = new HashMap<String, List<Object>>();

        /****************************************************************************************************
         * * * WARNING * * *
         * If the format/content of this map ever changes in a way that affects parsing on the receiving end,
         * the TICKETGRANTINGTICKET table needs to be completely cleared, since the old tickets stored there
         * will still have the old format
         ****************************************************************************************************/
        if (user != null) {
            resultsMap.put("id", Arrays.asList(new Object[]{String.valueOf(user.getId())}));
            resultsMap.put("displayName", Arrays.asList(new Object[]{user.getFirstName() + " " + user.getLastName()}));
            resultsMap.put("firstName", Arrays.asList(new Object[]{user.getFirstName()}));
            resultsMap.put("lastName", Arrays.asList(new Object[]{user.getLastName()}));
            resultsMap.put("email", Arrays.asList(new Object[]{user.getUsername()}));

            // We use a query instead of user.getAccounts() so that we only include enabled accounts
            List<UserAccount> accounts = userService.findByUserAndDisabled(user, false);
            JSONArray accountsArray = jsonHelper.buildUserAccountsJSON(accounts);
            // Get rid of the JSON-optional escaped slashes because Ruby's Psych parser chokes on them
            resultsMap.put("accounts", Arrays.asList(new Object[]{accountsArray.toJSONString().replaceAll("\\\\/", "/")}));

            resultsMap.put("authorities", new ArrayList<Object>(user.getAuthorities()));
        } else {
            logger.error("could not find a user record for: " + uid);
        }

        this.backingPerson = new AttributeNamedPersonImpl(resultsMap);
        return this.backingPerson;
    }

    public Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(Map<String, List<Object>> query) {
        if (query == null) {
            throw new IllegalArgumentException("Illegal to invoke getPeople(Map) with a null argument");
        }

        if (backingPerson == null) {
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
}
