package com.infusionsoft.cas.oauth.exceptions;

import org.springframework.http.HttpStatus;

/**
 * This is an exception that is used OAuth Error Responses
 *
 * @see <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1.2.1"></a>
 */
public class OAuthInvalidScopeException extends OAuthException {

    public OAuthInvalidScopeException() {
        super("invalid_scope", HttpStatus.BAD_REQUEST, "oauth.exception.invalid.scope");
    }

}
