package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.LoginAttempt;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Repository
public class LoginAttemptDAOImpl extends AbstractJpaDAO<LoginAttempt> implements LoginAttemptDAO {
    public LoginAttemptDAOImpl() {
        setClazz(LoginAttempt.class);
    }

    @Override
    public List<LoginAttempt> findByUsernameGreaterThanDateAttempted(String username, Date dateAttempted) {
        TypedQuery<LoginAttempt> query = entityManager.createQuery("SELECT la from LoginAttempt la where la.username = :username and la.dateAttempted > :dateAttempted order by la.dateAttempted desc", LoginAttempt.class);
        query.setParameter("username", username);
        query.setParameter("dateAttempted", dateAttempted);

        return query.getResultList();
    }

    @Override
    public List<LoginAttempt> findByLessThanDateAttempted(Date dateAttempted) {
        TypedQuery<LoginAttempt> query = entityManager.createQuery("SELECT la from LoginAttempt la where la.dateAttempted < :dateAttempted", LoginAttempt.class);
        query.setParameter("dateAttempted", dateAttempted);

        return query.getResultList();
    }
}
