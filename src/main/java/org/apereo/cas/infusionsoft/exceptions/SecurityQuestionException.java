package org.apereo.cas.infusionsoft.exceptions;

import org.jasig.cas.authentication.handler.AuthenticationException;

public class SecurityQuestionException extends AuthenticationException {
    private static final long serialVersionUID = 1L;

    public SecurityQuestionException(final String code) {
        super(code);
    }

    public SecurityQuestionException(final String code, final String msg) {
        super(code, msg);
    }

    public SecurityQuestionException(final String code, final String msg, final String type) {
        super(code, msg, type);
    }


}
