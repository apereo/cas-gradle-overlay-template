package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.User;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.StringWriter;

/**
 * Simple service for sending transactional emails, like the Forgot Password email.
 */
@Service
public class MailService {
    private static final Logger log = Logger.getLogger(MailService.class);

    @Value("${server.prefix}")
    private String serverPrefix = "";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    /**
     * Sends a welcome to new users who just created their Infusionsoft ID.
     */
    public void sendWelcomeEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setHeader("X-InfApp", "cas");
            message.setHeader("X-inf-package", "transactional");
            message.setHeader("Package", "transactional");
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

    /**
     * Sends an email with a link and password recovery code.
     */
    public void sendPasswordResetEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setHeader("X-InfApp", "cas");
            message.setHeader("X-inf-package", "transactional");
            message.setHeader("Package", "transactional");
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
}
