package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.CommunityAccountDetailsDAO;
import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.CommunityAccountDetails;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.AccountException;
import com.infusionsoft.cas.exceptions.UsernameTakenException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service for communicating with the Infusionsoft Community.
 */
@Service
@Transactional
public class CommunityServiceImpl implements CommunityService {
    private static final Logger log = Logger.getLogger(CommunityServiceImpl.class);

    @Autowired
    UserService userService;

    @Autowired
    CommunityAccountDetailsDAO communityAccountDetailsDAO;

    @Value("${infusionsoft.community.baseurl}")
    String communityBaseUrl;

    @Value("${infusionsoft.community.apikey}")
    String communityApiKey;

    /**
     * Builds a base URL to the Community for purposes of redirecting.
     */
    @Override
    public String buildUrl() {
        return communityBaseUrl + "/caslogin.php";
    }

    /**
     * Verifies a username and password with the Infusionsoft Community. Returns the community userid for that user.
     */
    @Override
    public String authenticateUser(String appUsername, String appPassword) {
        String userId = null;

        try {
            log.info("preparing REST call to " + communityBaseUrl);

            RestTemplate restTemplate = new RestTemplate();
            String md5password = DigestUtils.md5Hex(appPassword);
            String result = restTemplate.getForObject("{base}/rest.php/user/isvaliduser?key={apiKey}&username={appUsername}&md5password={md5password}", String.class, communityBaseUrl, communityApiKey, appUsername, md5password);

            log.debug("REST response from community: " + result);

            JSONObject returnValue = (JSONObject) JSONValue.parse(result);
            Boolean returnValid = (Boolean) returnValue.get("valid");

            userId = (String) returnValue.get("userid");

            if (returnValid == null || !returnValid) {
                log.warn("community user credentials for " + appUsername + " are invalid");
            }
        } catch (Exception e) {
            log.error("couldn't validate user credentials in community", e);
        }

        return userId;
    }

    /**
     * Calls out to the Community web service to try to create a new user. This is for users who create their Community
     * profile through CAS.
     */
    @Override
    public UserAccount registerCommunityUserAccount(User user, CommunityAccountDetails details, String ticketGrantingTicket) throws RestClientException, UsernameTakenException, AccountException {
        RestTemplate restTemplate = new RestTemplate();

        log.info("preparing REST call to " + communityBaseUrl);

        // TODO - retarded Restler won't read our params from the POST body, so we put them on the query string for now
        String username = details.getDisplayName();
        String email = StringUtils.isNotEmpty(details.getNotificationEmailAddress()) ? details.getNotificationEmailAddress() : user.getUsername();
        String response = restTemplate.postForObject("{base}/rest.php/user/addnewuser?key={apiKey}&username={username}&email={email}&experience={experience}&twitter={twitter}&timezone={timezone}", "", String.class, communityBaseUrl, communityApiKey, username, email, details.getInfusionsoftExperience(), details.getTwitterHandle(), details.getTimeZone());
        JSONObject responseJson = (JSONObject) JSONValue.parse(response);
        Boolean hasError = (Boolean) responseJson.get("error");
        String userId = String.valueOf(responseJson.get("userId"));

        if (hasError) {
            throw new UsernameTakenException("the display name " + details.getDisplayName() + " is already taken");
        }

        UserAccount account = userService.associateAccountToUser(user, AppType.COMMUNITY, "Infusionsoft Community", userId, ticketGrantingTicket);

        details.setUserAccount(account);
        communityAccountDetailsDAO.save(details);

        log.info("created community account details " + details.getId() + " for account " + account.getId());

        return account;
    }
}
