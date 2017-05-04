package org.apereo.cas.infusionsoft.support;

import com.github.inspektr.audit.AuditActionContext;
import com.github.inspektr.audit.AuditTrailManager;
import org.apereo.cas.infusionsoft.domain.AuditEntry;
import org.apereo.cas.infusionsoft.domain.AuditEntryType;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.services.AuditService;
import org.apereo.cas.infusionsoft.services.CasRegisteredServiceService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.services.RegisteredService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;

/**
 * Customized audit trail manager, so we can keep track of CAS events for auditing
 * and research purposes.
 */
public class InfusionsoftAuditTrailManager implements AuditTrailManager {
    private static final Logger log = Logger.getLogger(InfusionsoftAuditTrailManager.class);

    @Autowired
    private AuditService auditService;

    @Autowired
    private UserService userService;

    @Autowired
    private CasRegisteredServiceService registeredServiceService;

    /**
     * Handles an Inspektr audit action, logging it to our database if relevant.
     */
    public void record(AuditActionContext context) {
        String action = context.getActionPerformed();

        try {
            if (StringUtils.equals("AUTHENTICATION_SUCCESS", action)) {
                insertAuthenticationSuccessAuditEntry(context);
            } else if (StringUtils.equals("AUTHENTICATION_FAILED", action)) {
                insertAuthenticationFailedAuditEntry(context);
            } else if (StringUtils.equals("SERVICE_TICKET_CREATED", action)) {
                insertServiceTicketCreatedAuditEntry(context);
            }
        } catch (Exception e) {
            log.warn("failed to record audit entry for action " + action + ", principal " + context.getPrincipal(), e);
        }
    }

    /**
     * Creates an audit entry for authentication succeeding.
     */
    private void insertAuthenticationSuccessAuditEntry(AuditActionContext context) {
        AuditEntry entry = new AuditEntry();

        entry.setType(AuditEntryType.LoginSuccess);
        entry.setDate(DateTime.now(DateTimeZone.UTC));
        entry.setUserId(getUserIdForPrincipal(context.getPrincipal()));
        entry.setUsername(getUsernameForPrincipal(context.getPrincipal()));

        auditService.saveAuditEntry(entry);
    }

    /**
     * Creates an audit entry for authentication failing.
     */
    private void insertAuthenticationFailedAuditEntry(AuditActionContext context) {
        AuditEntry entry = new AuditEntry();

        entry.setType(AuditEntryType.LoginFail);
        entry.setDate(DateTime.now(DateTimeZone.UTC));
        entry.setUsername(getUsernameForPrincipal(context.getPrincipal()));

        auditService.saveAuditEntry(entry);
    }

    /**
     * Creates an audit entry for a new service ticket creation.
     */
    private void insertServiceTicketCreatedAuditEntry(AuditActionContext context) {
        AuditEntry entry = new AuditEntry();

        entry.setType(AuditEntryType.ServiceTicketCreated);
        entry.setDate(DateTime.now(DateTimeZone.UTC));
        entry.setUserId(getUserIdForPrincipal(context.getPrincipal()));
        entry.setUsername(getUsernameForPrincipal(context.getPrincipal()));

        // Try to extract the service URL and ID
        try {
            String[] chunks = context.getResourceOperatedUpon().split(" ", 3);

            if (chunks.length == 3) {
                URL url = new URL(chunks[2]);
                StringBuilder baseUrl = new StringBuilder(url.getProtocol());

                baseUrl.append("://").append(url.getHost().toLowerCase());

                if (url.getPort() != url.getDefaultPort() && url.getPort() != -1) {
                    baseUrl.append(":").append(url.getPort());
                }

                entry.setServiceBaseUrl(baseUrl.toString());

                RegisteredService registeredService = registeredServiceService.getRegisteredServiceByUrl(chunks[2]);

                if (registeredService != null) {
                    entry.setServiceId(registeredService.getId());
                }
            }
        } catch (Exception e) {
            log.debug("unable to parse service info from [" + context.getResourceOperatedUpon() + "]");
        }

        auditService.saveAuditEntry(entry);
    }

    /**
     * Attempts to resolve a numeric id for a user principal.
     */
    private Long getUserIdForPrincipal(String principal) {
        try {
            User user = userService.findEnabledUser(getUsernameForPrincipal(principal));

            if (user != null) {
                return user.getId();
            }
        } catch (Exception e) {
            log.debug("unable to resolve a user for principal [" + principal + "]", e);
        }

        return null;
    }

    /**
     * Attempts to parse the username from the principal (sometimes it has extra junk around it).
     */
    private String getUsernameForPrincipal(String principal) {
        try {
            if (principal.startsWith("[username: ")) {
                return principal.substring(11, principal.length() - 1);
            }
        } catch (Exception e) {
            log.debug("error parsing username from principal [" + principal + "]", e);
        }

        return principal;
    }
}
