package com.infusionsoft.cas.auth;

import java.net.URL;
import java.util.List;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.types.User;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Infusionsoft implementation of the authentication handler. It first attempts to authenticate the credentials
 * as a CAS user. If that fails, if there is a "service" parameter it will attempt to authenticate the user against
 * the referring app itself (supposing that they might be a legacy user).
 */
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
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

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    private boolean authenticateCasUser(InfusionsoftCredentials credentials) throws AuthenticationException {
        String encodedPassword = getPasswordEncoder().encode(credentials.getPassword());
        List<User> users = hibernateTemplate.find("from UserPassword p where p.user.username = ? and p.passwordEncoded = ? and p.active = true", credentials.getUsername(), encodedPassword);

        return users.size() > 0;
    }
}
