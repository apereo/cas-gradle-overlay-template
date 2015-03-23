package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.SecurityQuestionDAO;
import com.infusionsoft.cas.dao.SecurityQuestionResponseDAO;
import com.infusionsoft.cas.domain.SecurityQuestion;
import com.infusionsoft.cas.domain.SecurityQuestionResponse;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.exceptions.InfusionsoftValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("securityQuestionService")
@Transactional
public class SecurityQuestionServiceImpl implements SecurityQuestionService {

    @Value("${infusionsoft.cas.security.questions.force.answer}")
    private boolean forceSecurityQuestion;

    @Value("${infusionsoft.cas.security.questions.number.required}")
    int numSecurityQuestionsRequired;

    @Autowired
    private SecurityQuestionDAO securityQuestionDAO;

    @Autowired
    private SecurityQuestionResponseDAO securityQuestionResponseDAO;

    @Autowired
    UserService userService;

    @Override
    public SecurityQuestion save(SecurityQuestion securityQuestion) {
        return securityQuestionDAO.save(securityQuestion);
    }

    @Override
    public SecurityQuestionResponse save(SecurityQuestionResponse securityQuestionResponse) {
        return securityQuestionResponseDAO.save(securityQuestionResponse);
    }

    @Override
    public void delete(Long id) {
        SecurityQuestion securityQuestion = fetch(id);
        List<SecurityQuestionResponse> responses = securityQuestionResponseDAO.findAllBySecurityQuestion(securityQuestion);

        if(responses.size() > 0) {
            securityQuestion.setEnabled(false);
            securityQuestionDAO.save(securityQuestion);
        } else {
            securityQuestionDAO.delete(id);
        }
    }

    @Override
    public void deleteResponses(User user) throws InfusionsoftValidationException {
        List<SecurityQuestionResponse> securityQuestionResponses = securityQuestionResponseDAO.findAllByUser(user);
        user.getSecurityQuestionResponses().clear();
        userService.saveUser(user);
        securityQuestionResponseDAO.delete(securityQuestionResponses);
    }

    @Override
    public SecurityQuestion fetch(Long id) {
        return securityQuestionDAO.findOne(id);
    }

    @Override
    public List<SecurityQuestion> fetchAll() {
        Iterable<SecurityQuestion> securityQuestions = securityQuestionDAO.findAll();
        List<SecurityQuestion> retVal = new ArrayList<SecurityQuestion>();

        for (SecurityQuestion securityQuestion : securityQuestions) {
            retVal.add(securityQuestion);
        }

        return retVal;
    }

    @Override
    public List<SecurityQuestion> fetchAllEnabled() {
        Iterable<SecurityQuestion> securityQuestions = securityQuestionDAO.findAll();
        List<SecurityQuestion> retVal = new ArrayList<SecurityQuestion>();

        for (SecurityQuestion securityQuestion : securityQuestions) {
            retVal.add(securityQuestion);
        }

        return retVal;
    }

    @Override
    public SecurityQuestionResponse findAllResponsesById(Long id) {
        return securityQuestionResponseDAO.findOne(id);
    }

    @Override
    public List<SecurityQuestionResponse> findAllResponsesByUser(User user) {
        return securityQuestionResponseDAO.findAllByUser(user);
    }

    @Override
    public int getNumSecurityQuestionsRequired() {
        return numSecurityQuestionsRequired;
    }

    @Override
    public boolean isForceSecurityQuestion() {
        return forceSecurityQuestion;
    }
}
