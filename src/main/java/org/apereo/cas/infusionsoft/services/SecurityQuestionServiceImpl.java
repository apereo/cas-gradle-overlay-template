package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.dao.SecurityQuestionDAO;
import org.apereo.cas.infusionsoft.dao.SecurityQuestionResponseDAO;
import org.apereo.cas.infusionsoft.domain.SecurityQuestion;
import org.apereo.cas.infusionsoft.domain.SecurityQuestionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("securityQuestionService")
@Transactional
@Deprecated
public class SecurityQuestionServiceImpl implements SecurityQuestionService {

    @Value("${infusionsoft.cas.security.questions.force.answer}")
    private boolean forceSecurityQuestion;

    @Value("${infusionsoft.cas.security.questions.number.required}")
    int numSecurityQuestionsRequired;

    @Autowired
    private SecurityQuestionDAO securityQuestionDAO;

    @Autowired
    private SecurityQuestionResponseDAO securityQuestionResponseDAO;

    @Override
    public SecurityQuestionResponse save(SecurityQuestionResponse securityQuestionResponse) {
        return securityQuestionResponseDAO.save(securityQuestionResponse);
    }

    @Override
    public SecurityQuestion fetch(Long id) {
        return securityQuestionDAO.findOne(id);
    }

    @Override
    public List<SecurityQuestion> fetchAllEnabled() {
        Iterable<SecurityQuestion> securityQuestions = securityQuestionDAO.findAllByEnabledTrue();
        List<SecurityQuestion> retVal = new ArrayList<SecurityQuestion>();

        for (SecurityQuestion securityQuestion : securityQuestions) {
            retVal.add(securityQuestion);
        }

        return retVal;
    }

    @Override
    public int getNumSecurityQuestionsRequired() {
        return numSecurityQuestionsRequired;
    }

}
