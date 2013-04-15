package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserDAO extends PagingAndSortingRepository<User, Long> {
    User findByUsername(String username);

    User findByUsernameAndIdNot(String username, Long id);

    User findByUsernameAndEnabled(String username, Boolean enabled);

    User findByPasswordRecoveryCode(String passwordRecoveryCode);

    Page<User> findByUsernameLike(String username, Pageable pageable);
}
