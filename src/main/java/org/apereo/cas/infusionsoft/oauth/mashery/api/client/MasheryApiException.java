package org.apereo.cas.infusionsoft.oauth.mashery.api.client;

import org.apereo.cas.infusionsoft.oauth.mashery.api.domain.MasheryError;

import java.io.IOException;

public class MasheryApiException extends IOException {

    private final MasheryError masheryError;

    public MasheryApiException(MasheryError masheryError) {
        this.masheryError = masheryError;
    }

    public MasheryError getMasheryError() {
        return masheryError;
    }
}
