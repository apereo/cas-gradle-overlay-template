package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.OAuthServiceConfigDAO;
import com.infusionsoft.cas.domain.OAuthServiceConfig;
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