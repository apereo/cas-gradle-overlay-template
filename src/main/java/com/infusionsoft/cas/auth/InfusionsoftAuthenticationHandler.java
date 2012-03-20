package com.infusionsoft.cas.auth;

import java.util.List;

import com.infusionsoft.cas.types.User;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Infusionsoft implementation of the authentication handler.
 */
public class InfusionsoftAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
    private HibernateTemplate hibernateTemplate;

    protected boolean authenticateUsernamePasswordInternal(UsernamePasswordCredentials creds) throws AuthenticationException {
        List<User> users = hibernateTemplate.find("from User u where u.username = ?", creds.getUsername());

        if (users.size() > 0) {
            return true;
        }

        return false;
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
