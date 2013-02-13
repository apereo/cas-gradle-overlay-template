package com.infusionsoft.cas.services;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Service for communicating back and forth with CustomerHub.
 */
public class CustomerHubService {
    private static final Logger log = Logger.getLogger(CustomerHubService.class);

    private DefaultHttpClient customerHubHttpClient;

    private String customerHubDomain;
    private String customerHubApiUsername;
    private String customerHubApiPassword;
    private String customerHubProtocol;
    private int customerHubPort = 443;

    public String buildUrl(String appName) {
        return customerHubProtocol + "://" + appName + "." + customerHubDomain + ":" + customerHubPort;
    }

    /**
     * Authenticates a user with CustomerHub.
     */
    public boolean authenticateUser(String appName, String appUsername, String appPassword) {
        try {
            HttpClient client = getHttpClient(appName);
            HttpPost post = new HttpPost(buildUrl(appName) + "/account/authenticate_user");
            StringEntity input = new StringEntity(createAuthenticateUserRequest(appUsername, appPassword));

            post.addHeader("Accept", "application/json");
            post.setHeader("Content-Type", "application/json");
            input.setContentType("application/json");
            post.setEntity(input);

            log.debug("authenticating CustomerHub user " + appUsername + " at " + post.getURI());

            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();

            log.debug("authentication of CustomerHub user returned status " + status);

            EntityUtils.consume(response.getEntity());

            return status == 200;
        } catch (Exception e) {
            log.error("failed to authenticate CustomerHub user", e);
        }

        return false;
    }

    /**
     * Fetches a URL to a custom logo for a CustomerHub instance, or null if such a logo is unavailable.
     */
    public String getLogoUrl(String appName) {
        try {
            HttpClient client = getHttpClient(appName);
            HttpGet get = new HttpGet(buildUrl(appName) + "/account/logo");

            get.addHeader("Accept", "application/json");
            get.addHeader("Content-Type", "application/json");

            log.debug("getting CustomerHub logo at " + get.getURI());

            HttpResponse response = client.execute(get);
            int status = response.getStatusLine().getStatusCode();

            log.debug("logo request returned status " + status);

            if (status == 200) {
                return parseLogoResponse(response.getEntity().getContent());
            } else {
                EntityUtils.consume(response.getEntity());
            }
        } catch (Exception e) {
            log.error("failed to authenticate CustomerHub user", e);
        }

        return null;
    }

    public void setCustomerHubDomain(String customerHubDomain) {
        this.customerHubDomain = customerHubDomain;
    }

    public void setCustomerHubApiUsername(String customerHubApiUsername) {
        this.customerHubApiUsername = customerHubApiUsername;
    }

    public void setCustomerHubApiPassword(String customerHubApiPassword) {
        this.customerHubApiPassword = customerHubApiPassword;
    }

    public void setCustomerHubPort(int customerHubPort) {
        this.customerHubPort = customerHubPort;
    }

    public void setCustomerHubProtocol(String customerHubProtocol) {
        this.customerHubProtocol = customerHubProtocol;
    }

    private synchronized HttpClient getHttpClient(String appName) {
        if (customerHubHttpClient == null) {
            customerHubHttpClient = new DefaultHttpClient();
        }

        customerHubHttpClient.getCredentialsProvider().setCredentials(new AuthScope(appName + "." + customerHubDomain, customerHubPort), new UsernamePasswordCredentials(customerHubApiUsername, customerHubApiPassword));

        return customerHubHttpClient;
    }

    private String parseLogoResponse(InputStream input) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new InputStreamReader(input, "UTF-8"));

        return (String) json.get("logo");
    }

    private String createAuthenticateUserRequest(String appUsername, String appPassword) {
        JSONObject request = new JSONObject();

        request.put("email", appUsername);
        request.put("password", appPassword);

        log.debug("authentication request " + request.toJSONString());

        return request.toJSONString();
    }

    public static void main(String[] args) {
        Logger.getRootLogger().addAppender(new ConsoleAppender(new TTCCLayout()));
        Logger.getRootLogger().setLevel(Level.WARN);
        Logger.getLogger("com.infusionsoft").setLevel(Level.DEBUG);

        CustomerHubService service = new CustomerHubService();

        service.setCustomerHubDomain("customerhubtest.com");
        service.setCustomerHubPort(80);
        service.setCustomerHubProtocol("http");
        service.setCustomerHubApiUsername("manage");
        service.setCustomerHubApiPassword("9e88a5d9f7eb778d9400abcbd1362b017a47f7b907e3c23a26a004c10af4d38b");

        System.out.println("authenticated? " + service.authenticateUser("ndl", "nate@infusionsoft.com", "baylee00"));
        System.out.println("logo: " + service.getLogoUrl("ndl"));
    }
}
