package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserDAOImpl extends AbstractJpaDAO<User> implements UserDAO {
    public UserDAOImpl() {
        setClazz(User.class);
    }

    @Override
    public User findByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u where lower(u.username) = :username", User.class);
        query.setParameter("username", username);

        return getSingleRecord(query);
    }

    @Override
    public List<User> findByUsernameWildcard(String username) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u where lower(u.username) like :username", User.class);
        query.setParameter("username", username);

        return query.getResultList();
    }

    @Override
    public User findByUsernameAndNotId(String username, Long id) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u where lower(u.username) = :username and u.id <> :id", User.class);
        query.setParameter("username", username);
        query.setParameter("id", id);

        return getSingleRecord(query);
    }

    @Override
    public User findByUsernameAndEnabled(String username, Boolean enabled) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u where lower(u.username) = :username and u.enabled = :enabled", User.class);
        query.setParameter("username", username);
        query.setParameter("enabled", enabled);

        return getSingleRecord(query);
    }

    @Override
    public User findByPasswordRecoveryCode(String passwordRecoveryCode) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u from User u where u.passwordRecoveryCode = :passwordRecoveryCode", User.class);
        query.setParameter("passwordRecoveryCode", passwordRecoveryCode);

        return getSingleRecord(query);
    }
}
