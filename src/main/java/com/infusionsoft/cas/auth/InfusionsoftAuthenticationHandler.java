package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.types.User;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.List;

/**
 * Infusionsoft implementation of the authentication handler.
 */
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
    private HibernateTemplate hibernateTemplate;

    protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials creds) throws AuthenticationException {
        if (creds instanceof LetMeInCredentials) {
            return true;
        } else if (creds instanceof InfusionsoftCredentials) {
            InfusionsoftCredentials credentials = (InfusionsoftCredentials) creds;
            String encodedPassword = getPasswordEncoder().encode(credentials.getPassword());
            List<User> users = hibernateTemplate.find("from UserPassword p where lower(p.user.username) = ? and p.passwordEncoded = ? and p.active = true", credentials.getUsername().toLowerCase(), encodedPassword);

            if (users.size() > 0) {
                log.info("authenticated CAS user " + credentials.getUsername());

                return true;
            }
        } else {
            log.error("got some credentials we don't know how to authenticate! " + creds.getClass());
        }

        return false;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
