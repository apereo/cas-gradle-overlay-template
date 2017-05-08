package org.apereo.cas.infusionsoft.webflow;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.*;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.infusionsoft.authentication.InfusionsoftRegisteredServiceAccessStrategy;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.RegisteredServiceAccessStrategy;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.AbstractTicketException;
import org.apereo.cas.ticket.InvalidTicketException;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.security.GeneralSecurityException;

public class InfusionsoftPasswordExpirationEnforcementAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfusionsoftPasswordExpirationEnforcementAction.class);

    private final CentralAuthenticationService centralAuthenticationService;
    private final AuthenticationSystemSupport authenticationSystemSupport;
    private final TicketRegistrySupport ticketRegistrySupport;
    private final ServicesManager servicesManager;
    private final MessageSource messageSource;

    public InfusionsoftPasswordExpirationEnforcementAction(final AuthenticationSystemSupport authenticationSystemSupport,
                                                           final CentralAuthenticationService authenticationService,
                                                           final TicketRegistrySupport ticketRegistrySupport,
                                                           final ServicesManager servicesManager,
                                                           final MessageSource messageSource) {
        this.authenticationSystemSupport = authenticationSystemSupport;
        this.centralAuthenticationService = authenticationService;
        this.ticketRegistrySupport = ticketRegistrySupport;
        this.servicesManager = servicesManager;
        this.messageSource = messageSource;
    }

    /**
     * {@inheritDoc}
     * <p>
     * In the initial primary authentication flow, credentials are cached and available.
     * Since they are authenticated as part of submission first, there is no need to doubly
     * authenticate and verify credentials.
     * <p>
     * In subsequent authentication flows where a TGT is available and only an ST needs to be
     * created, there are no cached copies of the credential, since we do have a TGT available.
     * So we will simply grab the available authentication and produce the final result based on that.
     */
    @Override
    protected Event doExecute(final RequestContext context) throws GeneralSecurityException {
        final Service service = WebUtils.getService(context);
        final String ticketGrantingTicket = WebUtils.getTicketGrantingTicketId(context);

        try {
            final Authentication authentication = this.ticketRegistrySupport.getAuthenticationFrom(ticketGrantingTicket);
            if (authentication == null) {
                throw new InvalidTicketException(new AuthenticationException("No authentication found for ticket "
                        + ticketGrantingTicket), ticketGrantingTicket);
            }

            final RegisteredService registeredService = servicesManager.findServiceBy(service);

            if(registeredService != null) {
                final RegisteredServiceAccessStrategy strategy = registeredService.getAccessStrategy();

                if (strategy instanceof InfusionsoftRegisteredServiceAccessStrategy) {
                    InfusionsoftRegisteredServiceAccessStrategy infusionsoftRegisteredServiceAccessStrategy = (InfusionsoftRegisteredServiceAccessStrategy) strategy;

                    if (infusionsoftRegisteredServiceAccessStrategy.isForcePasswordExpiration()) {
                        final Credential credential = WebUtils.getCredential(context);
                        final AuthenticationResultBuilder builder = this.authenticationSystemSupport.establishAuthenticationContextFromInitial(authentication, credential);
                        final AuthenticationResult authenticationResult = builder.build(service);

                        Boolean passwordExpired = (Boolean) authenticationResult.getAuthentication().getPrincipal().getAttributes().get("passwordExpired");

                        if (passwordExpired != null && passwordExpired && infusionsoftRegisteredServiceAccessStrategy.isForcePasswordExpiration()) {
                            throw new AccountPasswordMustChangeException(messageSource.getMessage("login.passwordExpired", null, LocaleContextHolder.getLocale()));
                        }
                    }
                }
            }

            return success();

        } catch (final AbstractTicketException e) {
            if (e instanceof InvalidTicketException) {
                LOGGER.debug("CAS has determined ticket-granting ticket [{}] is invalid and must be destroyed", ticketGrantingTicket);
                this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicket);
            }

            return newEvent(CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, e);
        }
    }

    /**
     * New event based on the id, which contains an error attribute referring to the exception occurred.
     *
     * @param id    the id
     * @param error the error
     * @return the event
     */
    private Event newEvent(final String id, final Exception error) {
        return new EventFactorySupport().event(this, id, new LocalAttributeMap<>("error", error));
    }
}
