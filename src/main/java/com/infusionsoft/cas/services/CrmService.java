package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.exceptions.AppCredentialsExpiredException;
import com.infusionsoft.cas.exceptions.AppCredentialsInvalidException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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

    public List<String> extractAppNames(List<UserAccount> userAccounts) {
        List<String> appNames = new LinkedList<String>();

        for (UserAccount userAccount : userAccounts) {
            if (userAccount.getAppType().equals(AppType.CRM)) {
                appNames.add(buildCrmHostName(userAccount.getAppName()));
            }
        }

        return appNames;
    }
}
