package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.PendingUserAccount;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;

@Repository
public class PendingUserAccountDAOImpl extends AbstractJpaDAO<PendingUserAccount> implements PendingUserAccountDAO {
    public PendingUserAccountDAOImpl() {
        setClazz(PendingUserAccount.class);
    }

    @Override
    public PendingUserAccount findByAppTypeAndAppNameAndAppUsername(String appType, String appName, String appUsername) {
        TypedQuery<PendingUserAccount> query = entityManager.createQuery("SELECT pua from PendingUserAccount pua where pua.appType = :appType and pua.appName = :appName and pua.appUsername = :appUsername", PendingUserAccount.class);
        query.setParameter("appType", appType);
        query.setParameter("appName", appName);
        query.setParameter("appUsername", appUsername);

        return getSingleRecord(query);
    }

    @Override
    public PendingUserAccount findByRegistrationCode(String registrationCode) {
        TypedQuery<PendingUserAccount> query = entityManager.createQuery("SELECT pua from PendingUserAccount pua where pua.registrationCode = :registrationCode", PendingUserAccount.class);
        query.setParameter("registrationCode", registrationCode);

        return getSingleRecord(query);
    }

}
