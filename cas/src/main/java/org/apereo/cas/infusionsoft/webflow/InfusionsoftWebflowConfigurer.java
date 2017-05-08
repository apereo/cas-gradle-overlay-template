package org.apereo.cas.infusionsoft.webflow;

import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.web.flow.AbstractCasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.DecisionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.engine.support.TransitionExecutingFlowExecutionExceptionHandler;
import org.springframework.webflow.execution.Action;

public class InfusionsoftWebflowConfigurer extends AbstractCasWebflowConfigurer {

    private final Action infusionsoftFlowSetupAction;
    private final Action infusionsoftPasswordExpirationEnforcementAction;

    private static final String CAS_MUST_CHANGE_PASS_VIEW = "casMustChangePassView";

    public InfusionsoftWebflowConfigurer(final FlowBuilderServices flowBuilderServices, final FlowDefinitionRegistry loginFlowDefinitionRegistry, final Action infusionsoftFlowSetupAction, final Action infusionsoftPasswordExpirationEnforcementAction) {
        super(flowBuilderServices, loginFlowDefinitionRegistry);
        this.infusionsoftFlowSetupAction = infusionsoftFlowSetupAction;
        this.infusionsoftPasswordExpirationEnforcementAction = infusionsoftPasswordExpirationEnforcementAction;
    }

    @Override
    protected void doInitialize() throws Exception {
        final Flow loginFlow = getLoginFlow();
        if (loginFlow != null) {
            final ViewState state = (ViewState) loginFlow.getTransitionableState(CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
            state.getEntryActionList().add(this.infusionsoftFlowSetupAction);

            TransitionExecutingFlowExecutionExceptionHandler handler = new TransitionExecutingFlowExecutionExceptionHandler();
            handler.add(AccountPasswordMustChangeException.class, CAS_MUST_CHANGE_PASS_VIEW);

            loginFlow.getExceptionHandlerSet().add(handler);

            DecisionState serviceCheck = (DecisionState) loginFlow.getTransitionableState(CasWebflowConstants.STATE_ID_SERVICE_CHECK);
            serviceCheck.getEntryActionList().add(this.infusionsoftPasswordExpirationEnforcementAction);

            DecisionState hasServiceCheck = (DecisionState) loginFlow.getTransitionableState(CasWebflowConstants.STATE_ID_HAS_SERVICE_CHECK);
            hasServiceCheck.getEntryActionList().add(this.infusionsoftPasswordExpirationEnforcementAction);
        }
    }

}
