//package com.infusionsoft.cas.web.webflow;
//
//import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
//import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
//import org.jasig.cas.authentication.principal.Credentials;
//import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
//import org.jasig.cas.web.flow.AuthenticationViaFormAction;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.binding.message.MessageBuilder;
//import org.springframework.binding.message.MessageContext;
//import org.springframework.stereotype.Component;
//import org.springframework.webflow.execution.RequestContext;
//
//import java.net.MalformedURLException;
//import java.net.URL;
//
///**
//* Special action to replace the CAS default authentication action. If the built-in authentication fails,
//* it checks against the requested service to see if this is a valid legacy account. If so, redirects to the
//* registration pages so they can register and associate their legacy account to CAS.
//*/
//public class InfusionsoftAuthenticationViaFormAction extends AuthenticationViaFormAction {
//    private static final Logger log = Logger.getLogger(InfusionsoftAuthenticationViaFormAction.class);
//
//    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;
//
//    public String submitWithFallback(final RequestContext context, final Credentials creds, final MessageContext messageContext) throws Exception {
//        UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) creds;
//
//        log.info("processing form authentication for username " + credentials.getUsername());
//
//        // If the account is locked, stop right there.
//        if (infusionsoftAuthenticationService.isAccountLocked(credentials.getUsername())) {
//            try {
//                log.info("account is locked for username " + credentials.getUsername());
//                messageContext.addMessage(new MessageBuilder().error().code("login.lockedTooManyFailures").defaultText("login.lockedTooManyFailures").build());
//            } catch (final Exception e) {
//                log.error("couldn't build a useful message", e);
//            }
//
//            return "error";
//        }
//
//        // Attempt normal authentication with CAS.
//        String result = submit(context, creds, messageContext);
//
//        // If that failed, attempt the fallback. If that fails, set a helpful error message.
//        if (result.equals("success")) {
//            infusionsoftAuthenticationService.recordLoginAttempt(credentials, true);
//        } else if (result.equals("error") && authenticateWithApp(credentials, credentials.getService(), context)) {
//            log.info("username " + credentials.getUsername() + " was accepted by the app! let's have them register");
//
//            return "register";
//        } else if (result.equals("error")) {
//            infusionsoftAuthenticationService.recordLoginAttempt(credentials, false);
//
//            int recentFailures = Math.min(6, infusionsoftAuthenticationService.countConsecutiveFailedLogins(credentials.getUsername()));
//            String messageCode = "login.failed" + recentFailures;
//
//            messageContext.clearMessages();
//            messageContext.addMessage(new MessageBuilder().error().code(messageCode).defaultText(messageCode).build());
//        }
//
//        return result;
//    }
//
//    /**
//     * Attempts to validate the credentials against the legacy app/service instead. Returns true if successful.
//     */
//    private boolean authenticateWithApp(UsernamePasswordCredentials credentials, String service, RequestContext context) {
//        try {
//            log.debug("primary auth failed; attempting fallback authentication");
//
//            String appUsername = credentials.getUsername();
//            String appPassword = credentials.getPassword();
//            String appName = infusionsoftAuthenticationService.guessAppName(new URL(service));
//            String appType = infusionsoftAuthenticationService.guessAppType(new URL(service));
//
//            if (StringUtils.isNotEmpty(appName) && StringUtils.isNotEmpty(appType)) {
//                try {
//                    infusionsoftAuthenticationService.verifyAppCredentials(appType, appName, appUsername, appPassword);
//
//                    log.info("verified that " + appUsername + " is a legacy user at " + service);
//
//                    PendingUserAccount account = infusionsoftDataService.createPendingUserAccount(appType, appName, appUsername, "", "", "", false);
//
//                    log.info("created registration code " + account.getRegistrationCode());
//
//                    context.getRequestScope().put("registrationCode", account.getRegistrationCode());
//
//                    return true;
//                } catch (Exception e) {
//                    log.info("unable to verify " + appUsername + " at " + service);
//                }
//            } else {
//                log.info("not proceeding with fallback authentication: we couldn't parse an appName/appType from service url " + service);
//            }
//        } catch (MalformedURLException e) {
//            log.warn("skipping fallback authentication because there's no valid service url");
//        } catch (Exception e) {
//            log.warn("attempted fallback authentication, but failed for user " + credentials.getUsername(), e);
//        }
//
//        return false;
//    }
//
//    public void setInfusionsoftDataService(InfusionsoftDataService infusionsoftDataService) {
//        this.infusionsoftDataService = infusionsoftDataService;
//    }
//
//    public void setInfusionsoftAuthenticationService(InfusionsoftAuthenticationService infusionsoftAuthenticationService) {
//        this.infusionsoftAuthenticationService = infusionsoftAuthenticationService;
//    }
//}