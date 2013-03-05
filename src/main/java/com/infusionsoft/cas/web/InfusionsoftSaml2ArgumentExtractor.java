package com.infusionsoft.cas.web;

import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.web.support.AbstractSingleSignOutEnabledArgumentExtractor;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Simple argument extractor for SAMLv2 that uses our own service and response formats, since Mashery needs extra
 * claims beyond what the Google Accounts integration provides.
 */
public class InfusionsoftSaml2ArgumentExtractor extends AbstractSingleSignOutEnabledArgumentExtractor {
    @NotNull
    private PublicKey publicKey;

    @NotNull
    private PrivateKey privateKey;

    private String alternateUsername;

    public WebApplicationService extractServiceInternal(final HttpServletRequest request) {
        return InfusionsoftSaml2Service.createServiceFrom(request, this.privateKey, this.publicKey, this.alternateUsername);
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
}
