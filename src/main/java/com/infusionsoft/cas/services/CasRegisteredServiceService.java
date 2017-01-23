package com.infusionsoft.cas.services;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.authentication.principal.SimpleWebApplicationServiceImpl;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CasRegisteredServiceService {

    @Autowired
    private ServicesManager servicesManager;

    /**
     * Looks up a CAS registered service from a URL.  Finds the first service that matches and is enabled, or null if none is found.
     *
     * @param url url
     * @return RegisteredService
     */
    public RegisteredService getEnabledRegisteredServiceByUrl(String url) {
        RegisteredService registeredService = getRegisteredServiceByUrl(url);
        if (registeredService != null && !registeredService.isEnabled()) {
            registeredService = null;
        }
        return registeredService;
    }

    /**
     * Looks up a CAS registered service from a URL.  Finds the first service that matches, or null if none is found.
     *
     * @param url url
     * @return RegisteredService
     */
    public RegisteredService getRegisteredServiceByUrl(String url) {
        RegisteredService registeredService = null;
        if (StringUtils.isNotBlank(url)) {
            registeredService = servicesManager.findServiceBy(new SimpleWebApplicationServiceImpl(url));
        }
        return registeredService;
    }

}
