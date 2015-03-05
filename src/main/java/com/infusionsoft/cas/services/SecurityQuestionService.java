package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.SecurityQuestion;

import java.util.List;

public interface SecurityQuestionService {
    SecurityQuestion save(SecurityQuestion securityQuestion);

    void delete(Long id);

    SecurityQuestion fetch(Long id);

    List<SecurityQuestion> fetchAll();
}
