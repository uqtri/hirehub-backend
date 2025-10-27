package org.example.hirehub.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
public class EmailService {

    private JavaMailSender mailSender;

    @Value("${mailer.username}")
    private String fromAddress;
    EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendSimpleEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setSubject(subject);
        message.setText(body);
        message.setTo(to);
    }
    public void sendEmail(String to, String subject, String body,  boolean isHtml, String displayName) throws MessagingException {
        if (displayName == null || displayName.isBlank()) {
            displayName = "HireHub Support"; // default fallback
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(String.format("%s <%s>", displayName, fromAddress));
        helper.setSubject(subject);
        helper.setText(body, isHtml);
        helper.setTo(to);
        mailSender.send(mimeMessage);
    }
    public void sendEmailWithAttachment(String to, String subject, String body, boolean isHtml, File[] files) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom(fromAddress);
        helper.setSubject(subject);
        helper.setText(body, isHtml);
        helper.setTo(to);

        for (File file : files) {
            helper.addAttachment(file.getName(), file);
        }
        mailSender.send(mimeMessage);
    }
}
