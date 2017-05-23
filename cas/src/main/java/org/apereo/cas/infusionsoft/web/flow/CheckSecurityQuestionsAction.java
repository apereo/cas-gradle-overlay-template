package org.apereo.cas.infusionsoft.web.flow;

import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.services.SecurityQuestionService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

//TODO: upgrade: this class needs added to Spring config and the webflow
@Component
public class CheckSecurityQuestionsAction extends AbstractAction {

    @Autowired
    UserService userService;

    @Autowired
    SecurityQuestionService securityQuestionService;

    @Override
    protected Event doExecute(RequestContext requestContext) throws Exception {
        UsernamePasswordCredential usernamePasswordCredentials = (UsernamePasswordCredential) requestContext.getFlowScope().get("credentials");
        User user = userService.loadUser(usernamePasswordCredentials.getUsername());

        boolean userHasAnsweredQuestions = user.getSecurityQuestionResponses().size() >= securityQuestionService.getNumSecurityQuestionsRequired();

        if (userHasAnsweredQuestions) {
           return success();
        } else {
            return error();
        }
    }
}
