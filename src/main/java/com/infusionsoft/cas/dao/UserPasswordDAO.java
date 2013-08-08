package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserPassword;
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
