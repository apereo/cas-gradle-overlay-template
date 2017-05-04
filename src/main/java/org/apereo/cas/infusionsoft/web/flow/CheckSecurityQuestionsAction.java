package org.apereo.cas.infusionsoft.web.flow;

import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.services.SecurityQuestionService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

@Component
public class CheckSecurityQuestionsAction extends AbstractAction {

    @Autowired
    UserService userService;

    @Autowired
    SecurityQuestionService securityQuestionService;

    @Override
    protected Event doExecute(RequestContext requestContext) throws Exception {
        UsernamePasswordCredentials usernamePasswordCredentials = (UsernamePasswordCredentials) requestContext.getFlowScope().get("credentials");
        User user = userService.loadUser(usernamePasswordCredentials.getUsername());

        boolean userHasAnsweredQuestions = user.getSecurityQuestionResponses().size() >= securityQuestionService.getNumSecurityQuestionsRequired();

        if (userHasAnsweredQuestions) {
           return success();
        } else {
            return error();
        }
    }
}
