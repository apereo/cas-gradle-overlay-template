package com.infusionsoft.cas.dao;

import com.infusionsoft.cas.domain.SecurityQuestion;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SecurityQuestionDAO extends PagingAndSortingRepository<SecurityQuestion, Long> {

}
