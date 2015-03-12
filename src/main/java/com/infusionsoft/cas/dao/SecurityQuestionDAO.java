package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.SecurityQuestion;
import com.infusionsoft.cas.domain.SecurityQuestionResponse;
import com.infusionsoft.cas.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SecurityQuestionDAO extends PagingAndSortingRepository<SecurityQuestion, Long> {

    List<SecurityQuestion> findAllByEnabledTrue();

}
