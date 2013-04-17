package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserPassword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserPasswordDAO extends PagingAndSortingRepository<UserPassword, Long> {
    UserPassword findByUser_UsernameAndPasswordEncodedAndActiveTrue(String username, String encodedPassword);

    UserPassword findByUser_UsernameAndActiveTrue(String username);

    Page<UserPassword> findByUser(User user, Pageable pageable);
}
