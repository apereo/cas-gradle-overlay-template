//package com.infusionsoft.cas.web.webflow;
//
//import com.infusionsoft.cas.auth.PasswordPolicyEnforcementException;
//import com.infusionsoft.cas.auth.PasswordPolicyEnforcer;
//import org.jasig.cas.authentication.handler.AuthenticationException;
//import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;
//import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.binding.message.MessageBuilder;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//import org.springframework.webflow.action.AbstractAction;
//import org.springframework.webflow.execution.Event;
//import org.springframework.webflow.execution.RequestContext;
//
//@Component("passwordPolicyAction")
//public class PasswordPolicyEnforcementAction extends AbstractAction {
//    @Autowired
//    private PasswordPolicyEnforcer passwordPolicyEnforcer;
//
//    @Value("${infusionsoft.authentication.password.url}")
//    private String passwordPolicyUrl;
//
//    public final PasswordPolicyEnforcer getPasswordPolicyEnforcer() {
//        return this.passwordPolicyEnforcer;
//    }
//
//    private void populateErrorsInstance(final AuthenticationException e, final RequestContext reqCtx) {
//        try {
//            final String code = e.getCode();
//            reqCtx.getMessageContext().addMessage(new MessageBuilder().error().code(code).defaultText(code).build());
//        } catch (final Exception fe) {
//
//            if (this.logger.isErrorEnabled())
//                this.logger.error(fe.getMessage(), fe);
//
//        }
//    }
//
//    private final Event warning() {
//        return result("showWarning");
//    }
//
//    @Override
//    protected Event doExecute(final RequestContext context) throws Exception {
//
//        if (this.logger.isDebugEnabled())
//            this.logger.debug("Checking account status for password...");
//
//        final String ticket = context.getRequestScope().getString("serviceTicketId");
//        final UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) context.getFlowScope().get("credentials");
//        final String userId = credentials.getUsername();
//
//        Event returnedEvent = error();
//        String msgToLog = null;
//
//        try {
//
//            if (userId == null && ticket == null) {
//                msgToLog = "No user principal or service ticket available.";
//
//                if (this.logger.isErrorEnabled())
//                    this.logger.error(msgToLog);
//
//                throw new PasswordPolicyEnforcementException(BadCredentialsAuthenticationException.CODE, msgToLog);
//            }
//
//            if (userId == null && ticket != null) {
//
//                returnedEvent = success();
//
//                if (this.logger.isDebugEnabled())
//                    this.logger.debug("Received service ticket " + ticket
//                            + " but no user id. This is not a login attempt, so skip password enforcement.");
//
//            } else {
//
//                if (this.logger.isDebugEnabled())
//                    this.logger.debug("Retrieving number of days to password expiration date for user " + userId);
//
//                final long daysToExpirationDate = passwordPolicyEnforcer.getNumberOfDaysToPasswordExpirationDate(userId);
//
//                if (daysToExpirationDate == -1) {
//
//                    returnedEvent = success();
//
//                    if (this.logger.isDebugEnabled())
//                        this.logger.debug("Password for " + userId + " is not expiring");
//                } else {
//                    returnedEvent = warning();
//
//                    if (this.logger.isDebugEnabled())
//                        this.logger.debug("Password for " + userId + " is expiring in " + daysToExpirationDate + " days");
//
//                    context.getFlowScope().put("expireDays", daysToExpirationDate);
//                }
//
//            }
//        } catch (final AuthenticationException e) {
//            if (this.logger.isErrorEnabled())
//                this.logger.error(e.getMessage(), e);
//
//            populateErrorsInstance(e, context);
//            returnedEvent = error();
//        } finally {
//
//
//            if (this.logger.isDebugEnabled())
//                this.logger.debug("Switching to flow event id " + returnedEvent.getId() + " for user " + userId);
//        }
//
//        return returnedEvent;
//    }
//
//    @Override
//    protected void initAction() throws Exception {
//        Assert.notNull(passwordPolicyEnforcer, "password policy enforcer cannot be null");
//
//        if (this.logger.isDebugEnabled())
//            this.logger.debug("Initialized the action with password policy enforcer " + passwordPolicyEnforcer.getClass().getName());
//    }
//
//    public void setPasswordPolicyEnforcer(PasswordPolicyEnforcer passwordPolicyEnforcer) {
//        this.passwordPolicyEnforcer = passwordPolicyEnforcer;
//    }
//
//    public String getPasswordPolicyUrl() {
//        return passwordPolicyUrl;
//    }
//
//    public void setPasswordPolicyUrl(String passwordPolicyUrl) {
//        this.passwordPolicyUrl = passwordPolicyUrl;
//    }
//}
