package org.apereo.cas.infusionsoft.support;

import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.domain.AppType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAccountTransformer {

    private static final Logger log = LoggerFactory.getLogger(UserAccountTransformer.class);

    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    public UserAccountTransformer(InfusionsoftConfigurationProperties infusionsoftConfigurationProperties) {
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
                case CRM:
                    retVal = infusionsoftConfigurationProperties.getCrm().getUrl(appName);
                    break;

                case CUSTOMERHUB:
                    retVal = infusionsoftConfigurationProperties.getCustomerhub().getUrl(appName) + "/admin";
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
