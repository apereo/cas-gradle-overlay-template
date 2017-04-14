package org.apereo.cas.infusionsoft.config;

import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.apereo.cas.services.RegexRegisteredService;
import org.apereo.cas.services.RegisteredService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

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
        services.add(serviceFoundations());
        services.add(serviceCRM());

        return services;
    }

    @Bean
    RegisteredService serviceCAS() {
        return buildService(1, "Account Central", "https://(signin|devcas)\\.infusion(test|soft)\\.com(:[0-9]+)?/.*", 1);
    }

    @Bean
    RegisteredService serviceCustomerHub() {
        return buildService(3, "CustomerHub", "((https://.+\\.customerhub(\\.net|test\\.com))|(http://.+\\.customerhub.(dev|local)))(:[0-9]+)?/.*", 3);
    }

    @Bean
    RegisteredService serviceMarketplaceAPI() {
        return buildService(4, "Marketplace API", "https://marketplace3(dev)?\\.infusion(soft|test)\\.com(:[0-9]+)?/.*", 4);
    }

    @Bean
    RegisteredService serviceMarketplaceUI() {
        return buildService(5, "Marketplace UI", "https?://marketplace(3(dev)?ui)?\\.infusion(soft|test)\\.com(:[0-9]+)?((/.*)|$)", 5);
    }

    @Bean
    RegisteredService serviceCAM() {
        return buildService(6, "CAM", "https://cam\\.infusion(soft|test)\\.com/.*", 6);
    }

    @Bean
    RegisteredService serviceLocalhost() {
        return buildService(7, "Localhost", "https?://localhost(:[0-9]+)?/.*", 7);
    }

    @Bean
    RegisteredService serviceFoundations() {
        RegexRegisteredService service = buildService(8, "Foundations", "https?://.+\\.goldfishapp\\.co(:[0-9]+)?/.*", 8);

        DefaultRegisteredServiceProperty property = new DefaultRegisteredServiceProperty();
        property.getValues().add("true");

        service.getProperties().put("jwtAsResponse", property);

        return service;
    }

    @Bean
    RegisteredService serviceCRM() {
        // Must be last, because it's a wildcard on infusionsoft/test.com
        return buildService(9999, "Infusionsoft CRM", "https://.+\\.infusion(soft|test)\\.com(:[0-9]+)?/.*", 9999);
    }

    private RegexRegisteredService buildService(long id, String name, String serviceId, int evaluationOrder) {
        RegexRegisteredService service = new RegexRegisteredService();
        service.setId(id);
        service.setName(name);
        service.setServiceId(serviceId);
        service.setEvaluationOrder(evaluationOrder);

        return service;
    }

}
