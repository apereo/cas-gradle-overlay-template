package org.apereo.cas.infusionsoft.web.flow;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.infusionsoft.domain.SecurityQuestion;
import org.apereo.cas.infusionsoft.domain.SecurityQuestionResponse;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.services.SecurityQuestionService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.infusionsoft.web.controllers.commands.SetSecurityQuestionsForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

@Component
public class InfusionsoftSecurityQuestionsActionForm {

//      TODO: upgrade: replace with equivalent

    @Autowired
    SecurityQuestionService securityQuestionService;

    @Autowired
    UserService userService;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public final String submit(final Credential credential, final SetSecurityQuestionsForm securityQuestionsForm, final MessageContext messageContext) throws Exception {
        String retVal = "success";

//        if (securityQuestionService.isForceSecurityQuestion() || !securityQuestionsForm.isSkip()) {
        try {
            SecurityQuestion securityQuestion = securityQuestionService.fetch(securityQuestionsForm.getSecurityQuestionId());
            UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
            User user = userService.loadUser(usernamePasswordCredential.getUsername());

            SecurityQuestionResponse securityQuestionResponse = new SecurityQuestionResponse();
            securityQuestionResponse.setResponse(securityQuestionsForm.getResponse());
            securityQuestionResponse.setUser(user);
            securityQuestionResponse.setSecurityQuestion(securityQuestion);

            securityQuestionService.save(securityQuestionResponse);
        } catch (Exception e) {
            logger.error("Error saving security question response", e);
            messageContext.addMessage(new MessageBuilder().error().code("security.question.response.save.error").defaultText("Unable to save response").build());

            retVal = "error";
        }

        return retVal;
    }
}
