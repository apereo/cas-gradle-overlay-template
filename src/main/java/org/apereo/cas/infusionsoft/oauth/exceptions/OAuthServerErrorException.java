package org.apereo.cas.infusionsoft.oauth.exceptions;

import org.springframework.http.HttpStatus;

/**
 * This is an exception that is used OAuth Error Responses
 *
 * @see <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1.2.1"></a>
 */
public class OAuthServerErrorException extends OAuthException {

    public OAuthServerErrorException() {
        super("server_error", HttpStatus.BAD_REQUEST, "oauth.exception.server.error");
    }

    public OAuthServerErrorException(String message) {
        super("server_error", HttpStatus.BAD_REQUEST, message);
    }


    public OAuthServerErrorException(Exception e) {
        super("server_error", HttpStatus.BAD_REQUEST, e, "oauth.exception.server.error");
    }

}
