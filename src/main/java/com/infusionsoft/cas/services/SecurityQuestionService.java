package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.SecurityQuestion;
import com.infusionsoft.cas.domain.SecurityQuestionResponse;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.exceptions.InfusionsoftValidationException;

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
