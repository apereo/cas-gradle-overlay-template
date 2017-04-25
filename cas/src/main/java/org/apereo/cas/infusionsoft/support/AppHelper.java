package org.apereo.cas.infusionsoft.support;

import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.services.CrmService;
import org.apereo.cas.infusionsoft.services.CustomerHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppHelper {

    private static final Logger log = LoggerFactory.getLogger(AppHelper.class);

    private CrmService crmService;
    private CustomerHubService customerHubService;

    public AppHelper(CrmService crmService, CustomerHubService customerHubService) {
        this.crmService = crmService;
        this.customerHubService = customerHubService;
    }

    /**
     * Builds a URL for redirecting users to an app.
     *
     * @param appType appType
     * @param appName appName
     * @return redirect url
     */
    public String buildAppUrl(AppType appType, String appName) {
        String retVal = "";

        if (appType != null) {
            switch (appType) {
                case CAS:
                    retVal = "";
                    break;

                case CRM:
                    retVal = crmService.buildCrmUrl(appName);
                    break;

                case CUSTOMERHUB:
                    retVal = customerHubService.buildUrl(appName);
                    break;

                default:
                    log.warn("app url requested for unknown app type: " + appType);
                    retVal = "";
                    break;
            }
        }

        return retVal;
    }
}
