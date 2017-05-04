package org.apereo.cas.infusionsoft.exceptions;

import org.jasig.cas.authentication.handler.AuthenticationException;

public class PasswordPolicyEnforcementException extends AuthenticationException {
    private static final long serialVersionUID = 1L;

    public PasswordPolicyEnforcementException(final String code) {
        super(code);
    }

    public PasswordPolicyEnforcementException(final String code, final String msg) {
        super(code, msg);
    }

    public PasswordPolicyEnforcementException(final String code, final String msg, final String type) {
        super(code, msg, type);
    }


}
