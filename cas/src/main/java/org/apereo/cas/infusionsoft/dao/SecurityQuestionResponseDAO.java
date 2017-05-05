package org.apereo.cas.infusionsoft.dao;

import org.apereo.cas.infusionsoft.domain.SecurityQuestion;
import org.apereo.cas.infusionsoft.domain.SecurityQuestionResponse;
import org.apereo.cas.infusionsoft.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@Deprecated
public interface SecurityQuestionResponseDAO extends PagingAndSortingRepository<SecurityQuestionResponse, Long> {

    @Deprecated
    List<SecurityQuestionResponse> findAllByUser(User user);

    @Deprecated
    List<SecurityQuestionResponse> findAllBySecurityQuestion(SecurityQuestion securityQuestion);

}
