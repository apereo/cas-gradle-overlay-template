package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.OAuthClientDAO;
import com.infusionsoft.cas.domain.OAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuthClientService {
    @Autowired
    private OAuthClientDAO oAuthClientDAO;

    public OAuthClient loadOAuthClient (String clientId) {
        return oAuthClientDAO.findByClientId(clientId);
    }

    public boolean doesServiceMatchHeader(OAuthClient oAuthClient, String originHeader) {



        return true;
    }
}
