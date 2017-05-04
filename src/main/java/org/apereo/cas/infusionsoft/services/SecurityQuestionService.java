package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.SecurityQuestion;
import org.apereo.cas.infusionsoft.domain.SecurityQuestionResponse;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;

import java.util.List;

public interface SecurityQuestionService {
    SecurityQuestion save(SecurityQuestion securityQuestion);
    SecurityQuestionResponse save(SecurityQuestionResponse securityQuestionResponse);

    void delete(Long id);

    void deleteResponses(User user) throws InfusionsoftValidationException;

    SecurityQuestion fetch(Long id);

    List<SecurityQuestion> fetchAll();

    List<SecurityQuestion> fetchAllEnabled();

    SecurityQuestionResponse findAllResponsesById(Long id);

    List<SecurityQuestionResponse> findAllResponsesByUser(User user);

    int getNumSecurityQuestionsRequired();

    boolean isForceSecurityQuestion();
}
