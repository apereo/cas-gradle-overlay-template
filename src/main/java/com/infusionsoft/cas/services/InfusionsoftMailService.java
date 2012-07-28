package com.infusionsoft.cas.services;

import com.infusionsoft.cas.types.User;
import org.apache.log4j.Logger;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Simple service for sending transactional emails, like the Forgot Password email.
 */
public class InfusionsoftMailService {
    private static final Logger log = Logger.getLogger(InfusionsoftMailService.class);

    private MailSender mailSender;
    private SimpleMailMessage templateMessage;

    public void sendPasswordResetEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("info@infusionsoft.com");
            message.setTo(user.getUsername());
            message.setSubject("Your Infusionsoft Central password reset code");
            message.setText("Hi there, " + user.getFirstName() + "! Your password reset code is " + user.getPasswordRecoveryCode());

            mailSender.send(message);

            log.info("sent password recovery email to user " + user.getId());
        } catch (Exception e) {
            log.error("failed to send password recovery email to user " + user.getId() + "(" + user.getUsername() + ")", e);
        }
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTemplateMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }
}
