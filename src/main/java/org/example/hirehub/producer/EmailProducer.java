package org.example.hirehub.producer;

import org.example.hirehub.message.EmailMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import static org.example.hirehub.config.RabbitConfig.*;

@Service

public class EmailProducer {
    private final RabbitTemplate rabbitTemplate;

    EmailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    public void sendEmail(EmailMessage emailMessage) {
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, emailMessage);
    }
}
