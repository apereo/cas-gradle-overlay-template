package com.infusionsoft.cas.auth;

import java.util.Date;
import java.util.List;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.types.LoginAttempt;
import com.infusionsoft.cas.types.User;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Infusionsoft implementation of the authentication handler. It first attempts to authenticate the credentials
 * as a CAS user.
 */
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
    private HibernateTemplate hibernateTemplate;

    protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials creds) throws AuthenticationException {
        if (creds instanceof LetMeInCredentials) {
            return true;
        } else if (creds instanceof InfusionsoftCredentials) {
            InfusionsoftCredentials credentials = (InfusionsoftCredentials) creds;

            if (authenticateCasUser(credentials)) {
                log.info("authenticated CAS user " + credentials.getUsername());

                return true;
            }
        } else {
            log.warn("unexpected credentials type: " + creds.getClass());
        }

        return false;
    }

    private boolean authenticateCasUser(UsernamePasswordCredentials credentials) {
        String encodedPassword = getPasswordEncoder().encode(credentials.getPassword());
        List<User> users = hibernateTemplate.find("from UserPassword p where p.user.username = ? and p.passwordEncoded = ? and p.active = true", credentials.getUsername(), encodedPassword);

        return users.size() > 0;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
