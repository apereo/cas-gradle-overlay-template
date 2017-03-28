package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.SecurityQuestion;
import org.apereo.cas.infusionsoft.domain.SecurityQuestionResponse;
import org.apereo.cas.infusionsoft.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SecurityQuestionResponseDAO extends PagingAndSortingRepository<SecurityQuestionResponse, Long> {

    List<SecurityQuestionResponse> findAllByUser(User user);

    List<SecurityQuestionResponse> findAllBySecurityQuestion(SecurityQuestion securityQuestion);

}
