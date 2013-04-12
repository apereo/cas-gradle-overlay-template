package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserAccountDAOImpl extends AbstractJpaDAO<UserAccount> implements UserAccountDAO {
    public UserAccountDAOImpl() {
        setClazz(UserAccount.class);
    }

    @Override
    public List<UserAccount> findByUserAndDisabled(User user, Boolean disabled) {
        TypedQuery<UserAccount> query = entityManager.createQuery("SELECT ua FROM UserAccount ua WHERE ua.user = :user and ua.disabled = :disabled", UserAccount.class);
        query.setParameter("user", user);
        query.setParameter("disabled", disabled);

        return query.getResultList();
    }

    @Override
    public UserAccount findByUserAndId(User user, Long id) {
        TypedQuery<UserAccount> query = entityManager.createQuery("SELECT ua FROM UserAccount ua WHERE ua.user = :user and ua.id = :id", UserAccount.class);
        query.setParameter("user", user);
        query.setParameter("id", id);

        return getSingleRecord(query);
    }

    @Override
    public UserAccount findByUserAndAppNameAndAppTypeAndAppUsername(User user, String appName, String appType, String appUsername) {
        TypedQuery<UserAccount> query = entityManager.createQuery("SELECT ua FROM UserAccount ua WHERE ua.user = :user and ua.appName = :appName and ua.appType = :appType and ua.appUsername = :appUsername", UserAccount.class);
        query.setParameter("user", user);
        query.setParameter("appName", appName);
        query.setParameter("appType", appType);
        query.setParameter("appUsername", appUsername);

        return getSingleRecord(query);
    }

    @Override
    public List<UserAccount> findByUserAndAppTypeAndDisabled(User user, String appType, Boolean disabled) {
        TypedQuery<UserAccount> query = entityManager.createQuery("SELECT ua FROM UserAccount ua WHERE ua.user = :user and ua.appType = :appType and ua.disabled = :disabled", UserAccount.class);
        query.setParameter("user", user);
        query.setParameter("appType", appType);
        query.setParameter("disabled", disabled);

        return query.getResultList();
    }

    @Override
    public List<UserAccount> findByAppNameAndAppTypeAndDisabled(String appName, String appType, Boolean disabled) {
        TypedQuery<UserAccount> query = entityManager.createQuery("SELECT ua FROM UserAccount ua WHERE ua.appName = :appName and ua.appType = :appType and ua.disabled = :disabled", UserAccount.class);
        query.setParameter("appName", appName);
        query.setParameter("appType", appType);
        query.setParameter("disabled", disabled);

        return query.getResultList();
    }

    @Override
    public List<UserAccount> findByAppNameAndAppTypeAndAppUsernameAndDisabled(String appName, String appType, String appUsername, Boolean disabled) {
        TypedQuery<UserAccount> query = entityManager.createQuery("SELECT ua FROM UserAccount ua WHERE ua.appName = :appName and ua.appType = :appType and ua.appUsername = :appUsername and ua.disabled = :disabled", UserAccount.class);
        query.setParameter("appName", appName);
        query.setParameter("appType", appType);
        query.setParameter("appUsername", appUsername);
        query.setParameter("disabled", disabled);

        return query.getResultList();
    }


}
