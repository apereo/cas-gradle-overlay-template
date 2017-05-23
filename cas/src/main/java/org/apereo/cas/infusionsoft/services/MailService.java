package org.apereo.cas.infusionsoft.services;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.config.properties.InfusionsoftConfigurationProperties;
import org.apereo.cas.infusionsoft.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.Locale;

/**
 * Simple service for sending transactional emails, like the Forgot Password email.
 */
public class MailService {
    private static final String NO_REPLY_EMAIL_ADDRESS = "noreply@infusionsoft.com";
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private JavaMailSender mailSender;
    private VelocityEngine velocityEngine;
    private MessageSource messageSource;
    private InfusionsoftConfigurationProperties infusionsoftConfigurationProperties;
    private CasConfigurationProperties casConfigurationProperties;

    public MailService(JavaMailSender mailSender, VelocityEngine velocityEngine, MessageSource messageSource, InfusionsoftConfigurationProperties infusionsoftConfigurationProperties, CasConfigurationProperties casConfigurationProperties) {
        this.mailSender = mailSender;
        this.velocityEngine = velocityEngine;
        this.messageSource = messageSource;
        this.infusionsoftConfigurationProperties = infusionsoftConfigurationProperties;
        this.casConfigurationProperties = casConfigurationProperties;
    }

    /**
     * Utility method for making a transactional email in a "standard" manner.
     */
    private MimeMessage createMessage(User user, String emailAddress, String subject) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setHeader("X-InfApp", "cas");
        message.setHeader("X-inf-package", "transactional");
        message.setHeader("Package", "transactional");

        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(emailAddress);
        helper.setFrom(NO_REPLY_EMAIL_ADDRESS);
        helper.setSubject(subject);

        return message;
    }

    /**
     * Utility method for making a transactional email in a "standard" manner.
     */
    private MimeMessage createMessage(User user, String subject) throws MessagingException {
        return createMessage(user, user.getUsername(), subject);
    }

    /**
     * Sends a welcome to new users who just created their Infusionsoft ID.
     *
     * @param user   user
     * @param locale locale
     */
    public void sendWelcomeEmail(User user, Locale locale) {
        try {
            MimeMessage message = createMessage(user, messageSource.getMessage("email.welcome.subject", null, locale));

            StringWriter body = new StringWriter();
            Context context = new VelocityContext();

            context.put("user", user);

            velocityEngine.mergeTemplate("/velocity/welcomeEmail.vm", "UTF-8", context, body);
            message.setContent(body.toString(), "text/html");
            mailSender.send(message);

            log.info("sent welcome email to user " + user.getId());
        } catch (Exception e) {
            log.error("failed to send welcome email to user " + user.getId() + "(" + user.getUsername() + ")", e);
        }
    }

    /**
     * Sends an email with a link and password recovery code.
     *
     * @param user user
     */
    public void sendPasswordResetEmail(User user) {
        try {
            // we should be passing in a locale here, or be able to determine the user's locale from the user object...
            MimeMessage message = createMessage(user, messageSource.getMessage("email.reset.password.subject", null, Locale.getDefault()));

            StringWriter body = new StringWriter();
            Context context = new VelocityContext();

            context.put("user", user);
            context.put("code", user.getPasswordRecoveryCode());
            context.put("serverPrefix", casConfigurationProperties.getServer().getPrefix());
            context.put("supportPhoneNumbers", infusionsoftConfigurationProperties.getSupportPhoneNumbers());

            velocityEngine.mergeTemplate("/velocity/forgotPasswordEmail.vm", "UTF-8", context, body);
            message.setContent(body.toString(), "text/html");
            mailSender.send(message);

            log.info("sent password recovery email to user " + user.getId());
        } catch (Exception e) {
            log.error("failed to send password recovery email to user " + user.getId() + "(" + user.getUsername() + ")", e);
        }
    }

    /**
     * Sends an email informing user that their email has been changed.
     *
     * @param user user
     */
    @Deprecated
    public void sendInfusionsoftIdChanged(User user, String oldInfusionsoftId, boolean sendToOldEmail) {
        try {
            String to = sendToOldEmail ? oldInfusionsoftId : user.getUsername();
            // we should be passing in a locale here, or be able to determine the user's locale from the user object...
            MimeMessage message = createMessage(user, to, messageSource.getMessage("email.infusionsoftId.changed.subject", null, Locale.getDefault()));

            StringWriter body = new StringWriter();
            Context context = new VelocityContext();

            context.put("user", user);
            context.put("oldInfusionsoftId", oldInfusionsoftId);
            context.put("supportPhoneNumbers", infusionsoftConfigurationProperties.getSupportPhoneNumbers());

            velocityEngine.mergeTemplate("/velocity/infusionsoftIdChangedEmail.vm", "UTF-8", context, body);
            message.setContent(body.toString(), "text/html");
            mailSender.send(message);

            log.info("sent infusionsoft id changed email to user " + user.getId());
        } catch (Exception e) {
            log.error("failed to send infusionsoft id changed email to user " + user.getId() + "(" + user.getUsername() + ")", e);
        }
    }
}
