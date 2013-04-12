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
    CrmService crmService;

    @Autowired
    CommunityService communityService;

    @Autowired
    CustomerHubService customerHubService;

    /**
     * Builds a URL for redirecting users to an app.
     */
    public String buildAppUrl(String appType, String appName) {
        if (AppType.CRM.equals(appType)) {
            return crmService.buildCrmUrl(appName);
        } else if (AppType.COMMUNITY.equals(appType)) {
            return communityService.buildUrl();
        } else if (AppType.CUSTOMERHUB.equals(appType)) {
            return customerHubService.buildUrl(appName);
        } else {
            log.warn("app url requested for unknown app type: " + appType);

            return "";
        }
    }
}
