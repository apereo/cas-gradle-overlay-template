package com.infusionsoft.cas.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infusionsoft.cas.api.domain.UserAccountDTO;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.services.UserService;
import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.AbstractFlatteningPersonAttributeDao;
import org.jasig.services.persondir.support.AttributeNamedPersonImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    public AppHelper appHelper;

    public final static String[] POSSIBLE_VALUES = {"id", "accounts", "authorities", "displayName", "firstName", "lastName", "email"};
    public final static Set<String> ATTRIBUTE_NAMES = new HashSet<String>(Arrays.asList(POSSIBLE_VALUES));

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

        if (user != null) {
            resultsMap.put("id", Arrays.asList(new Object[]{String.valueOf(user.getId())}));
            resultsMap.put("displayName", Arrays.asList(new Object[]{user.getFirstName() + " " + user.getLastName()}));
            resultsMap.put("firstName", Arrays.asList(new Object[]{user.getFirstName()}));
            resultsMap.put("lastName", Arrays.asList(new Object[]{user.getLastName()}));
            resultsMap.put("email", Arrays.asList(new Object[]{user.getUsername()}));

            // We use a query instead of user.getAccounts() so that we only include enabled accounts
            List<UserAccount> accounts = userService.findActiveUserAccounts(user);
            resultsMap.put("accounts", Arrays.asList(new Object[]{getAccountsJSON(accounts)}));
            resultsMap.put("authorities", new ArrayList<Object>(user.getAuthorities()));
        } else {
            logger.error("could not find a user record for: " + uid);
        }

        return new AttributeNamedPersonImpl(resultsMap);
    }

    /**
     * *************************************************************************************************
     * * * WARNING * * *
     * If the format/content of this JSON ever changes in a way that affects parsing on the receiving end,
     * the TICKETGRANTINGTICKET table needs to be completely cleared, since the old tickets stored there
     * will still have the old format
     * **************************************************************************************************
     */
    private String getAccountsJSON(List<UserAccount> accounts) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserAccountDTO[] userAccounts = UserAccountDTO.convertFromCollection(accounts, appHelper);
            objectMapper.writeValue(outputStream, userAccounts);
        } catch (IOException e) {
            logger.error("Error while serializing accounts to JSON", e);
        }
        return outputStream.toString();
    }

    public Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(Map<String, List<Object>> query) {
        if (query == null) {
            throw new IllegalArgumentException("Illegal to invoke getPeople(Map) with a null argument");
        }

        return null;
    }

    public Set<String> getPossibleUserAttributeNames() {
        return ATTRIBUTE_NAMES;
    }

    public Set<String> getAvailableQueryAttributes() {
        return null;
    }
}
