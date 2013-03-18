package com.infusionsoft.cas.support;

import org.apache.log4j.Logger;
import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.web.support.AbstractSingleSignOutEnabledArgumentExtractor;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Simple argument extractor for SAMLv2 that uses our own service and response formats, since Mashery needs extra
 * claims beyond what the Google Accounts integration provides.
 */
public class InfusionsoftSaml2ArgumentExtractor extends AbstractSingleSignOutEnabledArgumentExtractor {
    private static final Logger log = Logger.getLogger(InfusionsoftSaml2ArgumentExtractor.class);

    @NotNull
    private PublicKey publicKey;

    @NotNull
    private PrivateKey privateKey;

    private String alternateUsername;

    private String serverPrefix;

    public WebApplicationService extractServiceInternal(final HttpServletRequest request) {
        InfusionsoftSaml2Service service = InfusionsoftSaml2Service.createServiceFrom(request, this.privateKey, this.publicKey, this.alternateUsername);

        if (service != null) {
            try {
                service.setIssuer(new URL(serverPrefix).getHost());
            } catch (Exception e) {
                log.error("unable to set SAMLv2 issuer from server prefix: " + serverPrefix, e);
            }
        }

        return service;
    }

    public void setPrivateKey(final PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(final PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setAlternateUsername(final String alternateUsername) {
        this.alternateUsername = alternateUsername;
    }

    public void setServerPrefix(String serverPrefix) {
        this.serverPrefix = serverPrefix;
    }
}
