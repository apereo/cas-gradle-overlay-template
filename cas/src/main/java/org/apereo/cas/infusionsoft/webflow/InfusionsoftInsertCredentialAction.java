package org.apereo.cas.infusionsoft.webflow;

import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.pm.PasswordManagementService;
import org.apereo.cas.pm.web.flow.PasswordChangeAction;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.flow.AbstractAuthenticationAction;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.apereo.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class InfusionsoftInsertCredentialAction extends AbstractAction {

    private TicketRegistry ticketRegistry;

    public InfusionsoftInsertCredentialAction(final TicketRegistry ticketRegistry) {
        this.ticketRegistry = ticketRegistry;
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) throws Exception {
        String tgt = WebUtils.getTicketGrantingTicketId(requestContext);
        TicketGrantingTicket ticket = ticketRegistry.getTicket(tgt, TicketGrantingTicket.class);

        if (ticket != null && !ticket.isExpired()) {
            String username = ticket.getAuthentication().getCredentials().get(0).getId();
            UsernamePasswordCredential usernamePasswordCredential = new UsernamePasswordCredential(username, null);

            WebUtils.putCredential(requestContext, usernamePasswordCredential);
        }

        return success();
    }
}
