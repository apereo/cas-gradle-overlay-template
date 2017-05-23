package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.SecurityQuestion;
import org.apereo.cas.infusionsoft.domain.SecurityQuestionResponse;

import java.util.List;

@Deprecated
public interface SecurityQuestionService {

    @Deprecated
    SecurityQuestionResponse save(SecurityQuestionResponse securityQuestionResponse);

    @Deprecated
    SecurityQuestion fetch(Long id);

    @Deprecated
    List<SecurityQuestion> fetchAllEnabled();

    @Deprecated
    int getNumSecurityQuestionsRequired();

}
