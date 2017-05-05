package org.apereo.cas.infusionsoft.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Service for communicating back and forth with CustomerHub.
 */
@Service
@Deprecated
public class CustomerHubService {
    private static final Logger log = Logger.getLogger(CustomerHubService.class);

    private DefaultHttpClient customerHubHttpClient;

    private String customerHubDomain;

    private String customerHubApiUsername;

    private String customerHubApiPassword;

    private String customerHubProtocol;

    private int customerHubPort = 443;

    @Value("${infusionsoft.customerhub.domain}")
    public void setCustomerHubDomain(String customerHubDomain) {
        this.customerHubDomain = customerHubDomain;
    }

    @Value("${infusionsoft.customerhub.api.username}")
    public void setCustomerHubApiUsername(String customerHubApiUsername) {
        this.customerHubApiUsername = customerHubApiUsername;
    }

    @Value("${infusionsoft.customerhub.api.password}")
    public void setCustomerHubApiPassword(String customerHubApiPassword) {
        this.customerHubApiPassword = customerHubApiPassword;
    }

    @Value("${infusionsoft.customerhub.protocol}")
    public void setCustomerHubProtocol(String customerHubProtocol) {
        this.customerHubProtocol = customerHubProtocol;
    }

    @Value("${infusionsoft.customerhub.port}")
    public void setCustomerHubPort(int customerHubPort) {
        this.customerHubPort = customerHubPort;
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
        StringBuilder baseUrl = new StringBuilder(customerHubProtocol + "://" + appName + "." + customerHubDomain);

        if (StringUtils.equals("http", customerHubProtocol) && customerHubPort != 80) {
            baseUrl.append(":").append(customerHubPort);
        } else if (StringUtils.equals("https", customerHubProtocol) && customerHubPort != 443) {
            baseUrl.append(":").append(customerHubPort);
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
            log.error("failed to authenticate CustomerHub user", e);
        }

        return null;
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

}
