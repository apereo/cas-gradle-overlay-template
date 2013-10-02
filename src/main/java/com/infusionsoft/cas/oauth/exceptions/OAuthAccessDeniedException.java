package com.infusionsoft.cas.oauth.exceptions;


public class OAuthAccessDeniedException extends  OAuthException {

    public OAuthAccessDeniedException() {
        super("access_denied");
    }

}
