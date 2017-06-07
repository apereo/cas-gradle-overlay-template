package org.apereo.cas.infusionsoft.webflow;

import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.pm.PasswordChangeBean;
import org.apereo.cas.pm.PasswordManagementService;
import org.apereo.cas.pm.web.flow.PasswordChangeAction;
import org.apereo.cas.pm.web.flow.PasswordManagementWebflowConfigurer;
import org.apereo.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class InfusionsoftPasswordChangeAction extends PasswordChangeAction {

    /**
     * Password Update Success event.
     */
    public static final String PASSWORD_UPDATE_SUCCESS = "passwordUpdateSuccess";

    private static final Logger LOGGER = LoggerFactory.getLogger(InfusionsoftPasswordChangeAction.class);

    private PasswordManagementService passwordManagementService;

    public InfusionsoftPasswordChangeAction(final PasswordManagementService passwordManagementService) {
        super(passwordManagementService);
        this.passwordManagementService = passwordManagementService;
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) throws Exception {
        try {
            String username = WebUtils.getAuthenticatedUsername();
            final UsernamePasswordCredential c = new UsernamePasswordCredential(username, null);
            final PasswordChangeBean bean = requestContext.getFlowScope()
                    .get(PasswordManagementWebflowConfigurer.FLOW_VAR_ID_PASSWORD, PasswordChangeBean.class);
            if (passwordManagementService.change(c, bean)) {
                return new EventFactorySupport().event(this, PASSWORD_UPDATE_SUCCESS);
            }
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        requestContext.getMessageContext().addMessage(new MessageBuilder().error().code("pm.updateFailure").
                defaultText("Could not update the account password").build());
        return error();
    }
}
