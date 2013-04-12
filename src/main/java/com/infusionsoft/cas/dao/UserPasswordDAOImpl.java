package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserPassword;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserPasswordDAOImpl extends AbstractJpaDAO<UserPassword> implements UserPasswordDAO {
    public UserPasswordDAOImpl() {
        setClazz(UserPassword.class);
    }

    @Override
    public UserPassword findByUsernameAndPassword(String username, String encodedPassword) {
        TypedQuery<UserPassword> query = entityManager.createQuery("SELECT p from UserPassword p where lower(p.user.username) = :username and p.passwordEncoded = :encodedPassword and p.active = true", UserPassword.class);
        query.setParameter("username", username);
        query.setParameter("encodedPassword", encodedPassword);

        return getSingleRecord(query);
    }

    @Override
    public UserPassword findByUsernameAndMD5Password(String username, String passwordEncodedMD5) {
        TypedQuery<UserPassword> query = entityManager.createQuery("SELECT p from UserPassword p where lower(p.user.username) = :username and p.passwordEncodedMD5 = :passwordEncodedMD5 and p.active = true and p.user.enabled = true", UserPassword.class);
        query.setParameter("username", username);
        query.setParameter("passwordEncodedMD5", passwordEncodedMD5);

        return getSingleRecord(query);
    }

    @Override
    public UserPassword findByUsername(String username) {
        TypedQuery<UserPassword> query = entityManager.createQuery("SELECT p from UserPassword p where lower(p.user.username) = :username and p.active = true order by p.dateCreated desc", UserPassword.class);
        query.setParameter("username", username);

        return getSingleRecord(query);
    }

    @Override
    public List<UserPassword> findLastFourByUsername(User user) {
        TypedQuery<UserPassword> query = entityManager.createQuery("SELECT p from UserPassword p where p.user = :user and p.active = true order by p.dateCreated desc", UserPassword.class);
        query.setParameter("user", user);
        query.setMaxResults(4);

        return query.getResultList();
    }
}
