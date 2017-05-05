package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.Authority;
import org.apereo.cas.infusionsoft.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserDAO extends PagingAndSortingRepository<User, Long> {
    @Deprecated
    User findByUsername(String username);

    @Deprecated
    User findByUsernameAndIdNot(String username, Long id);

    @Deprecated
    User findByUsernameAndEnabled(String username, Boolean enabled);

    User findByPasswordRecoveryCode(String passwordRecoveryCode);

    @Deprecated
    Page<User> findByUsernameLike(String username, Pageable pageable);

    @Deprecated
    @Query("select u from User u left join u.authorities as a where a = ?1 order by u.username")
    Page<User> findByAuthority(Authority authority, Pageable pageable);
}
