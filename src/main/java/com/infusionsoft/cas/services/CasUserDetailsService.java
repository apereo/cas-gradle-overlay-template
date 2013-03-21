package com.infusionsoft.cas.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import java.util.ArrayList;
import java.util.List;

public class CasUserDetailsService extends JdbcDaoImpl {
    @Override
    protected List<GrantedAuthority> loadUserAuthorities(String username) {
        List<GrantedAuthority> retVal = new ArrayList<GrantedAuthority>();

        retVal.add(new GrantedAuthorityImpl("ROLE_USER"));

        return retVal;
    }
}
