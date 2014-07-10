package com.infusionsoft.cas.oauth.exceptions;

import org.springframework.http.HttpStatus;

/**
 * This is an exception that is used OAuth Error Responses
 *
 * @see <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1.2.1"/>
 */
public class OAuthUnauthorizedClientException extends OAuthException {

    public OAuthUnauthorizedClientException() {
        super("unauthorized_client", HttpStatus.BAD_REQUEST, "oauth.exception.unauthorized.client");
    }

}
