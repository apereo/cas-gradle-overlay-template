package com.infusionsoft.cas.web.flow;

import org.jasig.cas.web.support.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

@Component
public class AddTicketGrantingTicketToFlowScopeAction extends AbstractAction {

    @Override
    protected Event doExecute(RequestContext requestContext) throws Exception {
        final String ticketGrantingTicketId = WebUtils.getTicketGrantingTicketId(requestContext);
        requestContext.getFlowScope().put("ticketGrantingTicketId", ticketGrantingTicketId);

        return success();
    }
}
