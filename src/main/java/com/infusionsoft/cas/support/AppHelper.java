package com.infusionsoft.cas.support;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.services.CommunityService;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.CustomerHubService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppHelper {

    private static final Logger log = Logger.getLogger(AppHelper.class);

    @Autowired
    public CrmService crmService;

    @Autowired
    public CommunityService communityService;

    @Autowired
    public CustomerHubService customerHubService;

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

                case COMMUNITY:
                    retVal = communityService.buildUrl();
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
