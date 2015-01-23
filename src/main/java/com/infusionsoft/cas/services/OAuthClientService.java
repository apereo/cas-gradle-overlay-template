package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.OAuthClientDAO;
import com.infusionsoft.cas.domain.OAuthClient;
import org.jasig.cas.services.RegisteredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuthClientService {

    @Autowired
    private OAuthClientDAO oAuthClientDAO;

    @Autowired
    private CasRegisteredServiceService registeredServiceService;

    public OAuthClient loadOAuthClient(String clientId) {
        return oAuthClientDAO.findByClientId(clientId);
    }

    public boolean isOriginAllowedByOAuthClient(OAuthClient oAuthClient, String originHeader) {
        RegisteredService registeredService = registeredServiceService.getEnabledRegisteredServiceByUrl(originHeader);
        return registeredService != null && registeredService.equals(oAuthClient.getRegisteredService());
    }

}
