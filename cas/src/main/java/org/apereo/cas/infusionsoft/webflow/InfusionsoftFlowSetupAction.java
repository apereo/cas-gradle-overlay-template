package org.apereo.cas.infusionsoft.webflow;

import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.infusionsoft.domain.AppType;
import org.apereo.cas.infusionsoft.services.BuildServiceImpl;
import org.apereo.cas.infusionsoft.services.InfusionsoftAuthenticationService;
import org.apereo.cas.infusionsoft.services.MarketingOptionsService;
import org.apereo.cas.infusionsoft.support.AppHelper;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.List;

public class InfusionsoftFlowSetupAction extends AbstractAction {

    private AppHelper appHelper;
    private BuildServiceImpl buildService;
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private MarketingOptionsService marketingOptionsService;
    private List<String> supportPhoneNumbers;

    public InfusionsoftFlowSetupAction(AppHelper appHelper, BuildServiceImpl buildService, InfusionsoftAuthenticationService infusionsoftAuthenticationService, MarketingOptionsService marketingOptionsService, List<String> supportPhoneNumbers) {
        this.appHelper = appHelper;
        this.buildService = buildService;
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
        this.marketingOptionsService = marketingOptionsService;
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
