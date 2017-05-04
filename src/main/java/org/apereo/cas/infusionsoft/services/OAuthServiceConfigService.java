package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.dao.OAuthServiceConfigDAO;
import org.apereo.cas.infusionsoft.domain.OAuthServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuthServiceConfigService {
    @Autowired
    private OAuthServiceConfigDAO oAuthServiceConfigDAO;

    public OAuthServiceConfig loadOAuthServiceConfig (String name) {
        return oAuthServiceConfigDAO.findByName(name);
    }
}