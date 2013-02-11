package com.infusionsoft.cas.services;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

/**
 * Service for communicating with the Infusionsoft CRM (aka "the app").
 */
public class CrmService {
    private static final Logger log = Logger.getLogger(CrmService.class);

    private String crmProtocol;
    private String crmDomain;
    private int crmPort;
    private String crmVendorKey;

    /**
     * Builds a base URL to a CRM app.
     */
    public String buildCrmUrl(String appName) {
        StringBuffer url = new StringBuffer(crmProtocol + "://" + appName + "." + crmDomain);

        if (crmProtocol.equals("http") && crmPort != 80) {
            url.append(":").append(crmPort);
        } else if (crmProtocol.equals("https") && crmPort != 443) {
            url.append(":").append(crmPort);
        }

        return url.toString();
    }

    /**
     * Verifies a username and password against a CRM app.
     */
    public boolean authenticateUser(String appName, String appUsername, String appPassword) {
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

                return true;
            } else {
                log.warn("unable to verify credentials! no temp key was returned for this username and password");
            }
        } catch (MalformedURLException e) {
            log.error("couldn't verify app credentials: xml-rpc url is invalid!", e);
        } catch (IOException e) {
            log.warn("web service call failed", e);
        } catch (XmlRpcException e) {
            log.info("app credentials are invalid", e);
        }

        return false;
    }

    public void setCrmProtocol(String crmProtocol) {
        this.crmProtocol = crmProtocol;
    }

    public void setCrmDomain(String crmDomain) {
        this.crmDomain = crmDomain;
    }

    public void setCrmPort(int crmPort) {
        this.crmPort = crmPort;
    }

    public void setCrmVendorKey(String crmVendorKey) {
        this.crmVendorKey = crmVendorKey;
    }
}
