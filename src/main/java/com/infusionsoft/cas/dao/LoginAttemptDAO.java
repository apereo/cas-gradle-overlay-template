package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.LoginAttempt;

import java.util.Date;
import java.util.List;

public interface LoginAttemptDAO extends JpaDAO<LoginAttempt> {
    List<LoginAttempt> findByUsernameGreaterThanDateAttempted(String username, Date dateAttempted);

    List<LoginAttempt> findByLessThanDateAttempted(Date dateAttempted);
}
