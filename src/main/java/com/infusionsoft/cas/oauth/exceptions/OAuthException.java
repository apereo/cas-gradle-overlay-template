package com.infusionsoft.cas.oauth.exceptions;

import java.io.IOException;

/**
 * This is an exception that is used OAuth Error Responses
 *
 * @see <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1.2.1"/>
 */
public abstract class OAuthException extends IOException {

    protected final String errorCode;

    protected OAuthException(String errorCode) {
        this.errorCode = errorCode;
    }

    protected OAuthException(String errorCode, Exception e) {
        super(e);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
