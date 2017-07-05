package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserPassword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserPasswordDAO extends PagingAndSortingRepository<UserPassword, Long> {
    UserPassword findFirstByUserAndPasswordEncodedOrderByDateCreatedDesc(User user, String encodedPassword);

    UserPassword findFirstByUserAndPasswordEncodedMD5OrderByDateCreatedDesc(User user, String encodedPassword);

    UserPassword findFirstByUserOrderByDateCreatedDesc(User user);

    Page<UserPassword> findByUser(User user, Pageable pageable);
}
