package com.infusionsoft.cas.services;

import com.infusionsoft.cas.exceptions.AccountException;
import com.infusionsoft.cas.exceptions.UsernameTakenException;
import com.infusionsoft.cas.types.AppType;
import com.infusionsoft.cas.types.CommunityAccountDetails;
import com.infusionsoft.cas.types.User;
import com.infusionsoft.cas.types.UserAccount;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service for communicating with the Infusionsoft Community.
 */
public class CommunityService {
    private static final Logger log = Logger.getLogger(CommunityService.class);

    private InfusionsoftDataService infusionsoftDataService;
    private HibernateTemplate hibernateTemplate;
    private String communityBaseUrl;
    private String communityApiKey;

    /**
     * Builds a base URL to the Community for purposes of redirecting.
     */
    public String buildUrl() {
        return communityBaseUrl + "/caslogin.php";
    }

    /**
     * Verifies a username and password with the Infusionsoft Community. Returns the community userid for that user.
     */
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

            if (returnValid == null || !returnValid.booleanValue()) {
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
    public UserAccount registerCommunityUserAccount(User user, CommunityAccountDetails details) throws RestClientException, UsernameTakenException, AccountException {
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

        UserAccount account = infusionsoftDataService.associateAccountToUser(user, AppType.COMMUNITY, "Infusionsoft Community", userId);

        details.setUserAccount(account);
        hibernateTemplate.save(details);

        log.info("created community account details " + details.getId() + " for account " + account.getId());

        return account;
    }

    public void setCommunityBaseUrl(String communityBaseUrl) {
        this.communityBaseUrl = communityBaseUrl;
    }

    public void setCommunityApiKey(String communityApiKey) {
        this.communityApiKey = communityApiKey;
    }

    public void setInfusionsoftDataService(InfusionsoftDataService infusionsoftDataService) {
        this.infusionsoftDataService = infusionsoftDataService;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
