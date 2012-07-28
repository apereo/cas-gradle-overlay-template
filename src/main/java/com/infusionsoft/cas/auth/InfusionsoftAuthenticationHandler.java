package com.infusionsoft.cas.auth;

import java.util.List;

import com.infusionsoft.cas.types.User;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Infusionsoft implementation of the authentication handler.
 */
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
    private HibernateTemplate hibernateTemplate;

    protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials creds) throws AuthenticationException {
        // TODO - this is the workaround for auto-login... not very secure
        if (creds.getPassword().equals("bogus")) {
            return true;
        }

        String encodedPassword = getPasswordEncoder().encode(creds.getPassword());
        List<User> users = hibernateTemplate.find("from User u where u.username = ? and u.password = ?", creds.getUsername(), encodedPassword);

        if (users.size() > 0) {
            return true;
        }

        return false;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
