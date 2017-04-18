package org.apereo.cas.infusionsoft.webflow;

import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftRegisteredServiceAccessStrategy;
import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.domain.MarketingOptions;
import org.apereo.cas.infusionsoft.services.BuildServiceImpl;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.MarketingOptionsService;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.List;

public class InfusionsoftFlowSetupAction extends AbstractAction {

    private AppHelper appHelper;
    private BuildServiceImpl buildService;
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private MarketingOptionsService marketingOptionsService;
    private ServicesManager servicesManager;
    private List<String> supportPhoneNumbers;

    @Autowired
    private MessageSource messageSource;

    @Value("${infusionsoft.account-central.baseUrl}")
    private String accountCentralUrl;

    public InfusionsoftFlowSetupAction(
            AppHelper appHelper,
            BuildServiceImpl buildService,
            InfusionsoftAuthenticationService infusionsoftAuthenticationService,
            MarketingOptionsService marketingOptionsService,
            ServicesManager servicesManager,
            List<String> supportPhoneNumbers
    ) {
        this.appHelper = appHelper;
        this.buildService = buildService;
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
        this.marketingOptionsService = marketingOptionsService;
        this.servicesManager = servicesManager;
        this.supportPhoneNumbers = supportPhoneNumbers;
    }

    @Override
    protected Event doExecute(RequestContext context) throws Exception {
        final MutableAttributeMap<Object> flowScope = context.getFlowScope();
        final WebApplicationService service = (WebApplicationService) flowScope.get("service");
        String appName = null;
        AppType appType = null;
        final MarketingOptions marketingOptions = marketingOptionsService.fetch();

        if (service != null) {
            appName = infusionsoftAuthenticationService.guessAppName(service.getOriginalUrl());
            appType = infusionsoftAuthenticationService.guessAppType(service.getOriginalUrl());
        }
        final String appUrl = appHelper.buildAppUrl(appType, appName);
        final String registrationUrl = accountCentralUrl + "/app/registration/createInfusionsoftId";

        final RegisteredService registeredService = this.servicesManager.findServiceBy(service);
        if (registeredService != null && registeredService.getAccessStrategy() instanceof InfusionsoftRegisteredServiceAccessStrategy) {
            InfusionsoftRegisteredServiceAccessStrategy strategy = (InfusionsoftRegisteredServiceAccessStrategy) registeredService.getAccessStrategy();
            flowScope.put("allowSocialLogin", strategy.isAllowSocialLogin());
        }

        flowScope.put("appName", appName);
        flowScope.put("appType", appType);
        flowScope.put("appUrl", appUrl);
        flowScope.put("appVersion", buildService.getBuildVersion());
        flowScope.put("enableAds", marketingOptions.getEnableAds());
        flowScope.put("adDesktopImageSrcUrl", marketingOptions.getDesktopImageSrcUrl());
        flowScope.put("adMobileImageSrcUrl", marketingOptions.getMobileImageSrcUrl());
        flowScope.put("adLinkUrl", marketingOptions.getHref());
        flowScope.put("supportPhoneNumbers", supportPhoneNumbers);
        flowScope.put("registrationUrl", registrationUrl);

        return success();
    }
}
