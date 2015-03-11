package com.infusionsoft.cas.web.flow;

import com.infusionsoft.cas.domain.SecurityQuestion;
import com.infusionsoft.cas.domain.SecurityQuestionResponse;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.services.SecurityQuestionService;
import com.infusionsoft.cas.services.UserService;
import com.infusionsoft.cas.web.controllers.commands.SetSecurityQuestionsForm;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InfusionsoftSecurityQuestionsActionForm {

    @Autowired
    SecurityQuestionService securityQuestionService;

    @Autowired
    UserService userService;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public final String submit(final Credentials credentials, final SetSecurityQuestionsForm securityQuestionsForm) throws Exception {
        String retVal;

        if (securityQuestionService.isForceSecurityQuestion() || !securityQuestionsForm.isSkip()) {
            try {
                SecurityQuestion securityQuestion = securityQuestionService.fetch(securityQuestionsForm.getSecurityQuestionId());
                UsernamePasswordCredentials usernamePasswordCredentials = (UsernamePasswordCredentials) credentials;
                User user = userService.loadUser(usernamePasswordCredentials.getUsername());

                SecurityQuestionResponse securityQuestionResponse = new SecurityQuestionResponse();
                securityQuestionResponse.setResponse(securityQuestionsForm.getResponse());
                securityQuestionResponse.setUser(user);
                securityQuestionResponse.setSecurityQuestion(securityQuestion);

                securityQuestionService.save(securityQuestionResponse);

                retVal = "success";
            } catch (Exception e) {
                logger.error("Error saving security question response", e);
                retVal = "error";
            }
        } else {
            retVal = "success";
        }

        return retVal;
    }
}
