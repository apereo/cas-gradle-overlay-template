package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Service for communicating with the Infusionsoft CRM (aka "the app").
 */
@Service
public class CrmService {

    private String crmProtocol;

    private String crmDomain;

    private int crmPort;

    @Value("${infusionsoft.crm.protocol}")
    public void setCrmProtocol(String crmProtocol) {
        this.crmProtocol = crmProtocol;
    }

    @Value("${infusionsoft.crm.domain}")
    public void setCrmDomain(String crmDomain) {
        this.crmDomain = crmDomain;
    }

    @Value("${infusionsoft.crm.port}")
    public void setCrmPort(int crmPort) {
        this.crmPort = crmPort;
    }

    /**
     * Builds a base URL to a CRM app.
     *
     * @param appName appName
     * @return redirect url
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
