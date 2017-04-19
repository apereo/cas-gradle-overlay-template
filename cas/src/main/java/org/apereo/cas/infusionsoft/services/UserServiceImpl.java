package org.apereo.cas.infusionsoft.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apereo.cas.api.UserAccountDTO;
import org.apereo.cas.infusionsoft.dao.AuthorityDAO;
import org.apereo.cas.infusionsoft.dao.UserAccountDAO;
import org.apereo.cas.infusionsoft.dao.UserDAO;
import org.apereo.cas.infusionsoft.dao.UserIdentityDAO;
import org.apereo.cas.infusionsoft.domain.Authority;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.apereo.cas.infusionsoft.domain.UserIdentity;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apereo.cas.infusionsoft.web.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(transactionManager = "transactionManager")
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private AppHelper appHelper;
    private AuthorityDAO authorityDAO;
    private UserDAO userDAO;
    private UserAccountDAO userAccountDAO;
    private UserIdentityDAO userIdentityDAO;

    public UserServiceImpl(AppHelper appHelper, AuthorityDAO authorityDAO, UserDAO userDAO, UserAccountDAO userAccountDAO, UserIdentityDAO userIdentityDAO) {
        this.appHelper = appHelper;
        this.authorityDAO = authorityDAO;
        this.userDAO = userDAO;
        this.userAccountDAO = userAccountDAO;
        this.userIdentityDAO = userIdentityDAO;
    }

    @Override
    public Map<String, Object> createAttributeMapForUser(@NotNull User user) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("id", user.getId());
        attributes.put("displayName", user.getFirstName() + " " + user.getLastName());
        attributes.put("firstName", user.getFirstName());
        attributes.put("lastName", user.getLastName());
        attributes.put("email", user.getUsername());

        // We use a query instead of user.getAccounts() so that we only include enabled accounts
        List<UserAccount> accounts = findActiveUserAccounts(user);
        attributes.put("accounts", getAccountsJSON(accounts));
        attributes.put("authorities", user.getAuthorities());

        return attributes;
    }

    @Override
    public List<UserAccount> findActiveUserAccounts(User user) {
        return userAccountDAO.findByUserAndDisabled(user, false);
    }

    @Override
    public Authority findAuthorityByName(String authorityName) {
        return authorityDAO.findByAuthority(authorityName);
    }

    @Override
    public User findUserByExternalId(String externalId) {
        User retVal = null;
        UserIdentity userIdentity = userIdentityDAO.findByExternalId(externalId);

        if (userIdentity != null) {
            retVal = userIdentity.getUser();
        }

        return retVal;
    }

    @Override
    public boolean isDuplicateUsername(User user) {
        if (user == null) {
            return false;
        } else if (user.getId() == null) {
            return userDAO.findByUsername(user.getUsername()) != null;
        } else {
            return userDAO.findByUsernameAndIdNot(user.getUsername(), user.getId()) != null;
        }
    }

    @Override
    public User loadUser(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public User saveUser(User user) throws InfusionsoftValidationException {
        boolean beingAdded = (user.getId() == null);
        if (beingAdded) {
            user.getAuthorities().add(findAuthorityByName("ROLE_CAS_USER"));
        }

        // Check if there is already a different user with the new username
        // NOTE: the Hibernate constraints will already force this, but I couldn't find a way to customize the exception message
        if (isDuplicateUsername(user)) {
            throw new InfusionsoftValidationException("user.error.email.inUse");
        }

        // NOTE: these are enforced by the annotation "@SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)" but this way the tags are just removed instead of throwing an error
        user.setFirstName(ValidationUtils.removeAllHtmlTags(user.getFirstName()));
        user.setLastName(ValidationUtils.removeAllHtmlTags(user.getLastName()));

        //TODO: if this user is currently logged in then change the object in the security context

        return userDAO.save(user);
    }

    @Override
    public UserIdentity saveUserIdentity(UserIdentity userIdentity) throws InfusionsoftValidationException {
        return userIdentityDAO.save(userIdentity);
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
        String json = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserAccountDTO[] userAccounts = UserAccountDTO.convertFromCollection(accounts, appHelper);
            objectMapper.writeValue(outputStream, userAccounts);
            json = outputStream.toString("UTF-8");
        } catch (Exception e) {
            log.error("Error while serializing accounts to JSON", e);
        }

        return json;
    }
}