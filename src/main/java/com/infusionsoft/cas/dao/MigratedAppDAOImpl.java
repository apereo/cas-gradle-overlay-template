package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.MigratedApp;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class MigratedAppDAOImpl extends AbstractJpaDAO<MigratedApp> implements MigratedAppDAO {
    public MigratedAppDAOImpl() {
        setClazz(MigratedApp.class);
    }

    @Override
    public List<MigratedApp> findByAppNameAndAppType(String appName, String appType) {
        TypedQuery<MigratedApp> query = entityManager.createQuery("SELECT ma from MigratedApp ma where ma.appName = :appName and ma.appType = :appType", MigratedApp.class);
        query.setParameter("appName", appName);
        query.setParameter("appType", appType);

        return query.getResultList();
    }
}
