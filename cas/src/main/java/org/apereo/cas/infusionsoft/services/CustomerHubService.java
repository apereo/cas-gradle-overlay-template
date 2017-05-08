package org.apereo.cas.infusionsoft.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.infusionsoft.config.properties.HostConfigurationProperties;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Service for communicating back and forth with CustomerHub.
 */
@Deprecated
public class CustomerHubService {

    private static final Logger log = LoggerFactory.getLogger(InfusionsoftPasswordManagementService.class);

    private DefaultHttpClient customerHubHttpClient;

    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    private HostConfigurationProperties customerHub;

    public CustomerHubService(InfusionsoftConfigurationProperties infusionsoftConfigurationProperties) {
        this.infusionsoftConfigurationProperties = infusionsoftConfigurationProperties;
        this.customerHub = infusionsoftConfigurationProperties.getCustomerhub();
    }

    /**
     * Builds a URL where users of this app should be sent after login.
     *
     * @param appName appName
     * @return redirect url
     */
    public String buildUrl(String appName) {
        return buildBaseUrl(appName) + "/admin";
    }

    /**
     * Builds a base URL for web services calls and what-not.
     *
     * @param appName appName
     * @return the base url
     */
    public String buildBaseUrl(String appName) {
        StringBuilder baseUrl = new StringBuilder(customerHub.getProtocol() + "://" + appName + "." + customerHub.getDomain());

        if (StringUtils.equals("http", customerHub.getProtocol()) && customerHub.getPort() != 80) {
            baseUrl.append(":").append(customerHub.getPort());
        } else if (StringUtils.equals("https", customerHub.getProtocol()) && customerHub.getPort() != 443) {
            baseUrl.append(":").append(customerHub.getPort());
        }

        return baseUrl.toString();
    }

    /**
     * Fetches a URL to a custom logo for a CustomerHub instance, or null if such a logo is unavailable.
     *
     * @param appName appName
     * @return logo url
     */
    public String getLogoUrl(String appName) {
        try {
            HttpClient client = getHttpClient(appName);
            HttpGet get = new HttpGet(buildBaseUrl(appName) + "/account/logo");

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
            log.error("failed to get CustomerHub logo", e);
        }

        return null;
    }

    private synchronized HttpClient getHttpClient(String appName) {
        if (customerHubHttpClient == null) {
            customerHubHttpClient = new DefaultHttpClient();
        }

        final UsernamePasswordCredential customerHubApi = infusionsoftConfigurationProperties.getCustomerHubApi();
        customerHubHttpClient.getCredentialsProvider().setCredentials(new AuthScope(appName + "." + customerHub.getDomain(), customerHub.getPort()), new UsernamePasswordCredentials(customerHubApi.getUsername(), customerHubApi.getPassword()));

        return customerHubHttpClient;
    }

    private String parseLogoResponse(InputStream input) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new InputStreamReader(input, "UTF-8"));

        return (String) json.get("logo");
    }

}
