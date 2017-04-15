package org.apereo.cas.infusionsoft.webflow;

import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftRegisteredServiceAccessStrategy;
import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.services.BuildServiceImpl;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.MarketingOptionsService;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.springframework.webflow.action.AbstractAction;
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
        WebApplicationService service = (WebApplicationService) context.getFlowScope().get("service");
        String appName = null;
        AppType appType = null;

        if (service != null) {
            appName = infusionsoftAuthenticationService.guessAppName(service.getOriginalUrl());
            appType = infusionsoftAuthenticationService.guessAppType(service.getOriginalUrl());
        }

        final RegisteredService registeredService = this.servicesManager.findServiceBy(service);
        if (registeredService != null && registeredService.getAccessStrategy() instanceof InfusionsoftRegisteredServiceAccessStrategy) {
            InfusionsoftRegisteredServiceAccessStrategy strategy = (InfusionsoftRegisteredServiceAccessStrategy) registeredService.getAccessStrategy();
            context.getFlowScope().put("allowSocialLogin", strategy.isAllowSocialLogin());
        }

        context.getFlowScope().put("appName", appName);
        context.getFlowScope().put("appType", appType);
        context.getFlowScope().put("appUrl", appHelper.buildAppUrl(appType, appName));
        context.getFlowScope().put("appVersion", buildService.getBuildVersion());
        context.getFlowScope().put("enableAds", marketingOptionsService.fetch().getEnableAds());
        context.getFlowScope().put("adDesktopImageSrcUrl", marketingOptionsService.fetch().getDesktopImageSrcUrl());
        context.getFlowScope().put("adMobileImageSrcUrl", marketingOptionsService.fetch().getMobileImageSrcUrl());
        context.getFlowScope().put("adLinkUrl", marketingOptionsService.fetch().getHref());
        context.getFlowScope().put("supportPhoneNumbers", supportPhoneNumbers);

        return success();
    }
}
