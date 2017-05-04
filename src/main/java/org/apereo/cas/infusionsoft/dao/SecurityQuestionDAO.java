package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.SecurityQuestion;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SecurityQuestionDAO extends PagingAndSortingRepository<SecurityQuestion, Long> {

    List<SecurityQuestion> findAllByEnabledTrue();

}
