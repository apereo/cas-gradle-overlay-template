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
        services.add(serviceCustomerHub());
        services.add(serviceMarketplaceAPI());
        services.add(serviceMarketplaceUI());
        services.add(serviceCAM());
        services.add(serviceLocalhost());
        services.add(servicePropel());
        services.add(serviceCRM());

        return services;
    }

    @Bean
    RegisteredService serviceCAS() {
        return buildService(1, "Account Central", "https://(signin|devcas)\\.infusion(test|soft)\\.com(:[0-9]+)?((/.*)|$)", 1);
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
        service.setTheme("cas-theme-propel");

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
        service.setAccessStrategy(new InfusionsoftRegisteredServiceAccessStrategy(true, true, false, true));
        service.setTheme("cas-theme-propel");

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
        return buildService(9999, "Infusionsoft CRM", "https://.+\\.infusion(soft|test)\\.com(:[0-9]+)?((/.*)|$)", 9999);
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
