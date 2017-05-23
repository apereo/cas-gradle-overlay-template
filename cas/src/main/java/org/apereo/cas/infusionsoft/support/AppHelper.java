package org.apereo.cas.infusionsoft.support;

import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.services.CrmService;
import org.apereo.cas.infusionsoft.services.CustomerHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class AppHelper {

    private static final Logger log = LoggerFactory.getLogger(AppHelper.class);

    private CrmService crmService;
    private CustomerHubService customerHubService;
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    public AppHelper(CrmService crmService, CustomerHubService customerHubService, InfusionsoftConfigurationProperties infusionsoftConfigurationProperties) {
        this.crmService = crmService;
        this.customerHubService = customerHubService;
        this.infusionsoftConfigurationProperties = infusionsoftConfigurationProperties;
    }

    /**
     * Builds a URL for redirecting users to an app.
     *
     * @param appType appType
     * @param appName appName
     * @return redirect url
     */
    @Deprecated
    public String buildAppUrl(AppType appType, String appName) {
        String retVal = "";

        if (appType != null) {
            switch (appType) {
                case CAS:
                    retVal = "";
                    break;

                case CRM:
                    retVal = crmService.buildUrl(appName);
                    break;

                case CUSTOMERHUB:
                    retVal = customerHubService.buildUrl(appName);
                    break;

                case COMMUNITY:
                    retVal = infusionsoftConfigurationProperties.getCommunity().getUrl();
                    break;

                case MARKETPLACE:
                    retVal = infusionsoftConfigurationProperties.getMarketplace().getUrl();
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
