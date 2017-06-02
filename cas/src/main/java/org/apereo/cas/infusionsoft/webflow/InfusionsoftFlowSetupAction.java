package org.apereo.cas.infusionsoft.webflow;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftRegisteredServiceAccessStrategy;
import org.apereo.cas.infusionsoft.domain.MarketingOptions;
import org.apereo.cas.infusionsoft.services.MarketingOptionsService;
import org.apereo.cas.infusionsoft.support.RegisteredServiceProperties;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceProperty;
import org.apereo.cas.services.ServicesManager;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class InfusionsoftFlowSetupAction extends AbstractAction {

    private BuildProperties buildProperties;
    private MarketingOptionsService marketingOptionsService;
    private ServicesManager servicesManager;
    private List<String> supportPhoneNumbers;

    public InfusionsoftFlowSetupAction(
            BuildProperties buildProperties,
            MarketingOptionsService marketingOptionsService,
            ServicesManager servicesManager,
            List<String> supportPhoneNumbers
    ) {
        this.buildProperties = buildProperties;
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

            final RegisteredServiceProperty affiliateUrl = registeredService.getProperties().get(RegisteredServiceProperties.AFFILIATE_URL);
            if (affiliateUrl != null && service != null) {
                try {
                    final URI serviceOriginalUrl = new URI(service.getOriginalUrl());
                    final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
                            .scheme(serviceOriginalUrl.getScheme())
                            .host(serviceOriginalUrl.getHost())
                            .port(serviceOriginalUrl.getPort())
                            .path(affiliateUrl.getValue());

                    flowScope.put("affiliateUrl", uriBuilder.toUriString());
                } catch (URISyntaxException e) {
                    logger.warn("Service URL not a valid URL", e);
                }
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
