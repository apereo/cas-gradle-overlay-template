package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.Authority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;

@Repository
public class AuthorityDAOImpl extends AbstractJpaDAO<Authority> implements AuthorityDAO {

    public AuthorityDAOImpl() {
        setClazz(Authority.class);
    }

    @Override
    public Authority getByAuthority(String authority) {
        TypedQuery<Authority> query = entityManager.createQuery("SELECT a from Authority a where a.authority = :authority", Authority.class);
        query.setParameter("authority", authority);

        return getSingleRecord(query);
    }
}
