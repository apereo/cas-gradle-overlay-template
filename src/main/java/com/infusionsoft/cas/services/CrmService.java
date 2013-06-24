package com.infusionsoft.cas.services;

import com.infusionsoft.cas.exceptions.AppCredentialsExpiredException;
import com.infusionsoft.cas.exceptions.AppCredentialsInvalidException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Vector;

/**
 * Service for communicating with the Infusionsoft CRM (aka "the app").
 */
@Service
public class CrmService {
    private static final Logger log = Logger.getLogger(CrmService.class);

    @Value("${infusionsoft.crm.protocol}")
    String crmProtocol;

    @Value("${infusionsoft.crm.domain}")
    String crmDomain;

    @Value("${infusionsoft.crm.port}")
    int crmPort;

    @Value("${infusionsoft.crm.vendorkey}")
    String crmVendorKey;

    /**
     * Builds a base URL to a CRM app.
     */
    public String buildCrmUrl(String appName) {
        StringBuilder url = new StringBuilder(crmProtocol + "://" + buildCrmHostName(appName));

        if (crmProtocol.equals("http") && crmPort != 80) {
            url.append(":").append(crmPort);
        } else if (crmProtocol.equals("https") && crmPort != 443) {
            url.append(":").append(crmPort);
        }

        return url.toString();
    }

    public String buildCrmHostName(String appName) {
        return appName + "." + crmDomain;
    }

    /**
     * Verifies a username and password against a CRM app.
     */
    public void authenticateUser(String appName, String appUsername, String appPassword) throws AppCredentialsInvalidException, AppCredentialsExpiredException {
        try {
            XmlRpcClient client = new XmlRpcClient(buildCrmUrl(appName) + "/api/xmlrpc");
            Vector<String> params = new Vector<String>();

            log.debug("attempting to verify crm credentials at url " + client.getURL() + " with vendor key " + crmVendorKey);

            params.add(crmVendorKey);
            params.add(appUsername);
            params.add(DigestUtils.md5Hex(appPassword));

            Object response = client.execute("DataService.getTemporaryKey", params);

            if (response != null) {
                log.info("getTemporaryKey returned a response " + response + " of type " + response.getClass());
            } else {
                throw new AppCredentialsInvalidException("no response! unable to verify crm credentials for " + appUsername + " on " + appName);
            }
        } catch (IOException e) {
            log.warn("web service call failed", e);

            throw new AppCredentialsInvalidException("unable to verify crm credentials for " + appUsername + " on " + appName, e);
        } catch (XmlRpcException e) {
            log.debug("app credentials are invalid", e);

            if (e.getMessage().contains("FailedLoginAttemptPasswordExpired")) {
                throw new AppCredentialsExpiredException("password is expired for " + appUsername + " on " + appName, e);
            } else {
                throw new AppCredentialsInvalidException("credentials are invalid for " + appUsername + " on " + appName, e);
            }
        }
    }
}
