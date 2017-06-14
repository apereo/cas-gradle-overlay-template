package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.infusionsoft.authentication.InfusionsoftRegisteredServiceAccessStrategy;
import org.apereo.cas.infusionsoft.support.RegisteredServiceProperties;
import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.apereo.cas.services.RegexRegisteredService;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class InfusionsoftRegisteredServicesConfiguration {

    @Bean
    List<RegisteredService> inMemoryRegisteredServices() {
        List<RegisteredService> services = new ArrayList<>();
        services.add(serviceCAS());
        services.add(serviceAccountCentral());
        services.add(serviceCustomerHub());
        services.add(serviceMarketplaceAPI());
        services.add(serviceMarketplaceUI());
        services.add(serviceCAM());
        services.add(serviceLocalhost());
        services.add(servicePropel());
        services.add(serviceEssentials());
        services.add(serviceCRM());

        return services;
    }

    @Bean
    RegisteredService serviceCAS() {
        return buildService(1, "CAS", "https://(signin|devcas)\\.infusion(test|soft)\\.com(:[0-9]+)?((/.*)|$)", 1);
    }

    @Bean
    RegisteredService serviceAccountCentral() {
        return buildService(2, "Account Central", "https://(accounts|devaccounts)\\.infusion(test|soft)\\.com(:[0-9]+)?((/.*)|$)", 2);
    }

    @Bean
    RegisteredService serviceCustomerHub() {
        return buildService(3, "CustomerHub", "((https://.+\\.customerhub(\\.net|test\\.com))|(http://.+\\.customerhub.(dev|local)))(:[0-9]+)?((/.*)|$)", 3);
    }

    @Bean
    RegisteredService serviceMarketplaceAPI() {
        return buildService(4, "Marketplace API", "https://marketplace3(dev)?\\.infusion(soft|test)\\.com(:[0-9]+)?((/.*)|$)", 4);
    }

    @Bean
    RegisteredService serviceMarketplaceUI() {
        return buildService(5, "Marketplace UI", "https?://marketplace(3(dev)?ui)?\\.infusion(soft|test)\\.com(:[0-9]+)?((/.*)|$)", 5);
    }

    @Bean
    RegisteredService serviceCAM() {
        return buildService(6, "CAM", "https://cam\\.infusion(soft|test)\\.com((/.*)|$)", 6);
    }

    @Bean
    RegisteredService serviceLocalhost() {
        RegexRegisteredService service =  buildService(7, "Localhost", "https?://localhost(:[0-9]+)?((/.*)|$)", 7);

        service.setAccessStrategy(new InfusionsoftRegisteredServiceAccessStrategy(true, true, false, true));
        service.setTheme("cas-theme-infusionsoft-design-2017");

        final Map<String, RegisteredServiceProperty> serviceProperties = service.getProperties();

        DefaultRegisteredServiceProperty property = new DefaultRegisteredServiceProperty();
        property.getValues().add("true");
        serviceProperties.put("jwtAsResponse", property);

        property = new DefaultRegisteredServiceProperty();
        property.getValues().add("true");
        serviceProperties.put(RegisteredServiceProperties.DISABLE_ADS, property);

        return service;
    }

    @Bean
    RegisteredService servicePropel() {
        RegexRegisteredService service = buildService(8, "Propel", "(mobile|https?)://([^\\.]+\\.goldfishapp\\.co|propel\\.infusion(soft|test)\\.com)(:[0-9]+)?((/.*)|$)", 8);
        service.setAccessStrategy(new InfusionsoftRegisteredServiceAccessStrategy(true, true, false, false));
        service.setTheme("cas-theme-infusionsoft-design-2017");

        final Map<String, RegisteredServiceProperty> serviceProperties = service.getProperties();

        DefaultRegisteredServiceProperty property = new DefaultRegisteredServiceProperty();
        property.getValues().add("true");
        serviceProperties.put("jwtAsResponse", property);

        property = new DefaultRegisteredServiceProperty();
        property.getValues().add("true");
        serviceProperties.put(RegisteredServiceProperties.DISABLE_ADS, property);

        return service;
    }

    @Bean
    RegisteredService serviceEssentials() {
        RegexRegisteredService service = buildService(9, "Essentials", "(mobile|https?)://((diamondback\\.infusion(soft|test))|(is-propel-web-[^\\./:]+\\.firebaseapp))\\.com(:[0-9]+)?((/.*)|$)", 9);
        service.setTheme("cas-theme-infusionsoft-design-2017");

        final Map<String, RegisteredServiceProperty> serviceProperties = service.getProperties();

        DefaultRegisteredServiceProperty property = new DefaultRegisteredServiceProperty();
        property.getValues().add("true");
        serviceProperties.put("jwtAsResponse", property);

        property = new DefaultRegisteredServiceProperty();
        property.getValues().add("true");
        serviceProperties.put(RegisteredServiceProperties.DISABLE_ADS, property);

        return service;
    }

    @Bean
    RegisteredService serviceCRM() {
        // Must be last, because it's a wildcard on infusionsoft/test.com
        final RegexRegisteredService service = buildService(9999, "Infusionsoft CRM", "https://.+\\.infusion(soft|test)\\.com(:[0-9]+)?((/.*)|$)", 9999);

        DefaultRegisteredServiceProperty property = new DefaultRegisteredServiceProperty();
        property.getValues().add("/Affiliate/");
        service.getProperties().put(RegisteredServiceProperties.AFFILIATE_URL, property);

        return service;
    }

    private RegexRegisteredService buildService(long id, String name, String serviceId, int evaluationOrder) {
        RegexRegisteredService service = new RegexRegisteredService();
        service.setId(id);
        service.setName(name);
        service.setServiceId(serviceId);
        service.setEvaluationOrder(evaluationOrder);
        service.setAccessStrategy(new InfusionsoftRegisteredServiceAccessStrategy(true, true, true, false));

        return service;
    }

}
