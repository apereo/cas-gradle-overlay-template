package org.apereo.cas.infusionsoft.config;

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
        services.add(serviceCRM());
        services.add(serviceCustomerHub());
        services.add(serviceMarketplaceAPI());
        services.add(serviceMarketplaceUI());
        services.add(serviceCAM());
        services.add(serviceLocalhost());

        return services;
    }

    @Bean
    RegisteredService serviceCAS() {
        return buildService(1, "Infusionsoft CAS", "https://(signin|devcas)\\.infusion(test|soft)\\.com(:[0-9]+)?/.*", 1);
    }

    @Bean
    RegisteredService serviceCRM() {
        return buildService(2, "Infusionsoft CRM", "https://.+\\.infusion(soft|test)\\.com(:[0-9]+)?/.*", 2);
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
        return buildService(5, "Marketplace API", "https?://marketplace(3(dev)?ui)?\\.infusion(soft|test)\\.com(:[0-9]+)?((/.*)|$)", 5);
    }

    @Bean
    RegisteredService serviceCAM() {
        return buildService(6, "CAM", "https://cam\\.infusion(soft|test)\\.com/.*", 6);
    }

    @Bean
    RegisteredService serviceLocalhost() {
        return buildService(7, "Localhost", "https?://localhost(:[0-9]+)?/.*", 7);
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
