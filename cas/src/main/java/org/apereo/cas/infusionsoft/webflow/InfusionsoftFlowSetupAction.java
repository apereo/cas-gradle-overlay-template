package org.apereo.cas.infusionsoft.webflow;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftRegisteredServiceAccessStrategy;
import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.domain.MarketingOptions;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.MarketingOptionsService;
import org.apereo.cas.infusionsoft.support.UserAccountTransformer;
import org.apereo.cas.infusionsoft.support.RegisteredServiceProperties;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceProperty;
import org.apereo.cas.services.ServicesManager;
import org.springframework.boot.info.BuildProperties;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.List;

public class InfusionsoftFlowSetupAction extends AbstractAction {

    private UserAccountTransformer userAccountTransformer;
    private BuildProperties buildProperties;
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private MarketingOptionsService marketingOptionsService;
    private ServicesManager servicesManager;
    private List<String> supportPhoneNumbers;

    public InfusionsoftFlowSetupAction(
            UserAccountTransformer userAccountTransformer,
            BuildProperties buildProperties,
            InfusionsoftAuthenticationService infusionsoftAuthenticationService,
            MarketingOptionsService marketingOptionsService,
            ServicesManager servicesManager,
            List<String> supportPhoneNumbers
    ) {
        this.userAccountTransformer = userAccountTransformer;
        this.buildProperties = buildProperties;
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
        this.marketingOptionsService = marketingOptionsService;
        this.servicesManager = servicesManager;
        this.supportPhoneNumbers = supportPhoneNumbers;
    }

    @Override
    protected Event doExecute(RequestContext context) throws Exception {
        final MutableAttributeMap<Object> flowScope = context.getFlowScope();
        final WebApplicationService service = (WebApplicationService) flowScope.get("service");
        String registrationUrl = "/registration/createInfusionsoftId";
        if (service != null) {
            String registrationParam  = context.getRequestParameters().get("registration");
            if (StringUtils.isNotBlank(registrationParam) && servicesManager.findServiceBy(registrationParam) != null) {
                registrationUrl = registrationParam;
            }
            AppType appType = infusionsoftAuthenticationService.guessAppType(service.getOriginalUrl());
            if (appType == AppType.CRM) {
                String appName = infusionsoftAuthenticationService.guessAppName(service.getOriginalUrl());
                flowScope.put("crmAffiliateUrl", userAccountTransformer.buildAppUrl(appType, appName) + "/Affiliate/");
            }
        }

        final MarketingOptions marketingOptions = marketingOptionsService.fetch();
        boolean enableAds = marketingOptions.getEnableAds();
        final RegisteredService registeredService = this.servicesManager.findServiceBy(service);
        if (registeredService != null) {
            if (registeredService.getAccessStrategy() instanceof InfusionsoftRegisteredServiceAccessStrategy) {
                InfusionsoftRegisteredServiceAccessStrategy strategy = (InfusionsoftRegisteredServiceAccessStrategy) registeredService.getAccessStrategy();
                flowScope.put("allowSocialLogin", strategy.isAllowSocialLogin());
            }

            final RegisteredServiceProperty disableAds = registeredService.getProperties().get(RegisteredServiceProperties.DISABLE_ADS);
            if (disableAds != null) {
                enableAds = enableAds && !Boolean.parseBoolean(disableAds.getValue());
            }
        }

        flowScope.put("registrationUrl", registrationUrl);
        flowScope.put("adDesktopImageSrcUrl", marketingOptions.getDesktopImageSrcUrl());
        flowScope.put("adLinkUrl", marketingOptions.getHref());
        flowScope.put("adMobileImageSrcUrl", marketingOptions.getMobileImageSrcUrl());
        flowScope.put("appVersion", buildProperties.getVersion());
        flowScope.put("enableAds", enableAds);
        flowScope.put("supportPhoneNumbers", supportPhoneNumbers);

        return success();
    }
}
