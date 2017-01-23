package com.infusionsoft.cas.oauth.exceptions;

import org.springframework.http.HttpStatus;

/**
 * This is an exception that is used OAuth Error Responses
 *
 * @see <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1.2.1"></a>
 */
public class OAuthInvalidRequestException extends OAuthException {

    public OAuthInvalidRequestException() {
        super("invalid_request", HttpStatus.BAD_REQUEST, "oauth.exception.invalid.request");
    }

    public OAuthInvalidRequestException(String message) {
        super("invalid_request", HttpStatus.BAD_REQUEST, message);
    }

}
