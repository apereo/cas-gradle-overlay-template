package com.infusionsoft.cas.services;

import com.infusionsoft.cas.dao.SecurityQuestionDAO;
import com.infusionsoft.cas.domain.SecurityQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("securityQuestionService")
@Transactional
public class SecurityQuestionServiceImpl implements SecurityQuestionService {

    @Autowired
    private SecurityQuestionDAO securityQuestionDAO;

    @Override
    public SecurityQuestion save(SecurityQuestion securityQuestion) {
        return securityQuestionDAO.save(securityQuestion);
    }

    @Override
    public void delete(Long id) {
        securityQuestionDAO.delete(id);
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
}
