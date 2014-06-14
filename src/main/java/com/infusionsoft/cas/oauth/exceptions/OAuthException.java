package com.infusionsoft.cas.oauth.exceptions;

import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * This is an exception that is used OAuth Error Responses
 *
 * @see <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1.2.1"/>
 */
public abstract class OAuthException extends IOException {

    protected final String errorCode;
    protected final HttpStatus httpStatus;
    protected final String errorDescription;
    protected final String errorUri;

    protected OAuthException(String errorCode, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorDescription = "";
        this.errorUri = "";
    }

    protected OAuthException(String errorCode, HttpStatus httpStatus, String errorDescription, String errorUri) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorDescription = errorDescription;
        this.errorUri = errorUri;
    }

    protected OAuthException(String errorCode, HttpStatus httpStatus, Exception e) {
        super(e);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorDescription = "";
        this.errorUri = "";
    }

    protected OAuthException(String errorCode, HttpStatus httpStatus, Exception e, String errorDescription, String errorUri) {
        super(e);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorDescription = errorDescription;
        this.errorUri = errorUri;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getErrorUri() {
        return errorUri;
    }
}
