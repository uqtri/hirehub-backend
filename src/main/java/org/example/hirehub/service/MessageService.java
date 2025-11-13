package org.example.hirehub.service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.example.hirehub.dto.message.CreateMessageDTO;
import org.example.hirehub.entity.Message;
import org.example.hirehub.entity.User;
import org.example.hirehub.exception.UserHandlerException;
import org.example.hirehub.repository.MessageRepository;
import org.example.hirehub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Setter
@Getter
@Service
public class MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public MessageService(UserRepository userRepository, MessageRepository messageRepository){
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;

    }
    @Transactional
    public Message createMessage(CreateMessageDTO message) {
        User sender = userRepository.findByEmail(message.getSenderEmail());
        User receiver = userRepository.findByEmail(message.getReceiverEmail());

        Message newMsg = new Message();
        newMsg.setSender(sender);
        newMsg.setReceiver(receiver);
        newMsg.setMessage(message.getMessage());
        newMsg.setCreatedAt(LocalDateTime.now());

        return messageRepository.save(newMsg);
    }
}
