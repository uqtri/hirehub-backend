package org.example.hirehub.consumer;

import jakarta.mail.MessagingException;
import org.example.hirehub.message.EmailMessage;
import org.example.hirehub.service.AuthService;
import org.example.hirehub.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import static org.example.hirehub.config.RabbitConfig.*;

@Service
public class EmailConsumer {

    EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }
    @RabbitListener(queues = EMAIL_QUEUE)
    public void handleSendingEmail(EmailMessage emailMessage) throws MessagingException {

        String to = emailMessage.getTo();
        String subject = emailMessage.getSubject();
        String body = emailMessage.getBody();
        boolean isHtml = emailMessage.isHtml();
        emailService.sendEmail(to, subject, body, isHtml, "HireHub Support");
    }
}
