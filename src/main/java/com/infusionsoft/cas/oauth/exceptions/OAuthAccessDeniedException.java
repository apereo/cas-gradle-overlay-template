package com.infusionsoft.cas.oauth.exceptions;

import org.springframework.http.HttpStatus;

public class OAuthAccessDeniedException extends OAuthException {

    public OAuthAccessDeniedException() {
        super("access_denied", HttpStatus.FORBIDDEN);
    }

}
