package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.dao.SecurityQuestionDAO;
import org.apereo.cas.infusionsoft.dao.SecurityQuestionResponseDAO;
import org.apereo.cas.infusionsoft.domain.SecurityQuestion;
import org.apereo.cas.infusionsoft.domain.SecurityQuestionResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional(transactionManager = "transactionManager")
@Deprecated
public class SecurityQuestionServiceImpl implements SecurityQuestionService {

    private SecurityQuestionDAO securityQuestionDAO;
    private SecurityQuestionResponseDAO securityQuestionResponseDAO;
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;

    public SecurityQuestionServiceImpl(SecurityQuestionDAO securityQuestionDAO, SecurityQuestionResponseDAO securityQuestionResponseDAO, InfusionsoftConfigurationProperties infusionsoftConfigurationProperties) {
        this.securityQuestionDAO = securityQuestionDAO;
        this.securityQuestionResponseDAO = securityQuestionResponseDAO;
        this.infusionsoftConfigurationProperties = infusionsoftConfigurationProperties;
    }

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
        List<SecurityQuestion> retVal = new ArrayList<>();

        for (SecurityQuestion securityQuestion : securityQuestions) {
            retVal.add(securityQuestion);
        }

        return retVal;
    }

    @Override
    public int getNumSecurityQuestionsRequired() {
        return infusionsoftConfigurationProperties.getNumSecurityQuestionsRequired();
    }

}
