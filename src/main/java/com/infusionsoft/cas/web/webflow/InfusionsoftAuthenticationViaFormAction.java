package com.infusionsoft.cas.web.webflow;

import com.infusionsoft.cas.auth.InfusionsoftCredentials;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import com.infusionsoft.cas.services.InfusionsoftDataService;
import com.infusionsoft.cas.types.PendingUserAccount;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.web.flow.AuthenticationViaFormAction;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.execution.RequestContext;

import java.net.URL;

/**
 * Special action to replace the CAS default authentication action. If the built-in authentication fails,
 * it checks against the requested service to see if this is a valid legacy account. If so, redirects to the
 * registration pages so they can register and associate their legacy account to CAS.
 */
public class InfusionsoftAuthenticationViaFormAction extends AuthenticationViaFormAction {
    private static final Logger log = Logger.getLogger(InfusionsoftAuthenticationViaFormAction.class);

    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
    private InfusionsoftDataService infusionsoftDataService;

    public String submitWithFallback(final RequestContext context, final Credentials creds, final MessageContext messageContext) throws Exception {
        String result = submit(context, creds, messageContext);

        if (result.equals("error")) {
            if (creds instanceof InfusionsoftCredentials) {
                InfusionsoftCredentials credentials = (InfusionsoftCredentials) creds;

                try {
                    log.debug("primary auth failed; attempting fallback authentication");

                    String service = credentials.getService();
                    String appUsername = credentials.getUsername();
                    String appPassword = credentials.getPassword();
                    String appName = infusionsoftAuthenticationService.guessAppName(new URL(service));
                    String appType = infusionsoftAuthenticationService.guessAppType(new URL(service));

                    if (StringUtils.isNotEmpty(appName) && StringUtils.isNotEmpty(appType)) {
                        boolean valid = infusionsoftAuthenticationService.verifyAppCredentials(appType, appName, appUsername, appPassword);

                        if (valid) {
                            log.info("verified that " + appUsername + " is a legacy user at " + service);

                            PendingUserAccount account = infusionsoftDataService.createPendingUserAccount(appType, appName, appUsername);

                            log.info("created registration code " + account.getRegistrationCode());

                            context.getRequestScope().put("registrationCode", account.getRegistrationCode());

                            return "register";
                        } else {
                            log.info("unable to verify " + appUsername + " at " + service);
                        }
                    } else {
                        log.info("not proceeding with fallback authentication: we couldn't parse an appName/appType from service url " + service);
                    }
                } catch (Exception e) {
                    log.warn("attempted fallback authentication, but failed for user " + credentials.getUsername(), e);
                }
            }
        }

        return result;
    }

    private boolean authenticateLegacyUser(InfusionsoftCredentials credentials) throws AuthenticationException {
        log.debug("attempting to pass-through authenticate user with service " + credentials.getService());

        try {
            String appName = infusionsoftAuthenticationService.guessAppName(new URL(credentials.getService()));
            String appType = infusionsoftAuthenticationService.guessAppType(new URL(credentials.getService()));
            boolean valid = infusionsoftAuthenticationService.verifyAppCredentials(appType, appName, credentials.getUsername(), credentials.getPassword());

            if (valid) {
                // TODO - hang on to them so we can complete proper registration/linkage
            }

            return valid;
        } catch (Exception e) {
            log.error("failed to do pass-through authentication to app", e);
        }

        return false;
    }

    public void setInfusionsoftDataService(InfusionsoftDataService infusionsoftDataService) {
        this.infusionsoftDataService = infusionsoftDataService;
    }

    public void setInfusionsoftAuthenticationService(InfusionsoftAuthenticationService infusionsoftAuthenticationService) {
        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
    }
}