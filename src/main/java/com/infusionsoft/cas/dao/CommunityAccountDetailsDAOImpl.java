package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.CommunityAccountDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CommunityAccountDetailsDAOImpl extends AbstractJpaDAO<CommunityAccountDetails> implements CommunityAccountDetailsDAO {
    public CommunityAccountDetailsDAOImpl() {
        setClazz(CommunityAccountDetails.class);
    }
}
