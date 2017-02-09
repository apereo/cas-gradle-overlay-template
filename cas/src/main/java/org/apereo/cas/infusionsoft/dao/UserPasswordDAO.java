package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.domain.UserPassword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserPasswordDAO extends PagingAndSortingRepository<UserPassword, Long> {
    List<UserPassword> findByUserAndPasswordEncodedOrderByActiveDescDateCreatedDesc(User user, String encodedPassword);

    List<UserPassword> findByUserAndPasswordEncodedMD5OrderByActiveDescDateCreatedDesc(User user, String md5EncodedPassword);

    UserPassword findByUserAndActiveTrue(User user);

    Page<UserPassword> findByUser(User user, Pageable pageable);
}
