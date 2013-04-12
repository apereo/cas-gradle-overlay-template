package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.Authority;

public interface AuthorityDAO extends JpaDAO<Authority> {

    Authority getByAuthority(String authority);

}
