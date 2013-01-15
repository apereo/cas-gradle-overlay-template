package com.infusionsoft.cas.services;

import com.infusionsoft.cas.types.User;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;

/**
 * Simple service for sending transactional emails, like the Forgot Password email.
 */
public class InfusionsoftMailService {
    private static final Logger log = Logger.getLogger(InfusionsoftMailService.class);

    private String serverPrefix = "";
    private JavaMailSender mailSender;
    private SimpleMailMessage templateMessage;
    private VelocityEngine velocityEngine;

    public void sendWelcomeEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setTo(user.getUsername());
            helper.setFrom("noreply@infusionsoft.com");
            helper.setSubject("Infusionsoft ID Confirmation - Please save this email");

            StringWriter body = new StringWriter();
            Context context = new VelocityContext();

            context.put("user", user);
            context.put("serverPrefix", serverPrefix);

            velocityEngine.mergeTemplate("/velocity/welcomeEmail.vm", "UTF-8", context, body);
            message.setContent(body.toString(), "text/html");
            mailSender.send(message);

            log.info("sent welcome email to user " + user.getId());
        } catch (Exception e) {
            log.error("failed to send welcome email to user " + user.getId() + "(" + user.getUsername() + ")", e);
        }
    }

    public void sendPasswordResetEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setTo(user.getUsername());
            helper.setFrom("noreply@infusionsoft.com");
            helper.setSubject("Reset your password at Infusionsoft Account Central");

            StringWriter body = new StringWriter();
            Context context = new VelocityContext();

            context.put("user", user);
            context.put("code", user.getPasswordRecoveryCode());
            context.put("serverPrefix", serverPrefix);

            velocityEngine.mergeTemplate("/velocity/forgotPasswordEmail.vm", "UTF-8", context, body);
            message.setContent(body.toString(), "text/html");
            mailSender.send(message);

            log.info("sent password recovery email to user " + user.getId());
        } catch (Exception e) {
            log.error("failed to send password recovery email to user " + user.getId() + "(" + user.getUsername() + ")", e);
        }
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTemplateMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public void setServerPrefix(String serverPrefix) {
        this.serverPrefix = serverPrefix;
    }
}
