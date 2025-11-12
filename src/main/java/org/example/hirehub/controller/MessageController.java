package org.example.hirehub.controller;

import org.example.hirehub.dto.message.CreateMessageDTO;
import org.example.hirehub.dto.message.MessageDetailDTO;
import org.example.hirehub.entity.Message;
import org.example.hirehub.repository.MessageRepository;
import org.example.hirehub.service.MessageService;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")

@Controller
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final MessageService messageService;

    public MessageController(SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository, MessageService messageService) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/chat/private") //client send to /app/chat/private
    public void processPrivateMessage(@Payload CreateMessageDTO chatMsg) {
        messageService.createMessage(chatMsg);

        messagingTemplate.convertAndSendToUser(chatMsg.getSenderEmail(),"/queue/messages", chatMsg);
        messagingTemplate.convertAndSendToUser(chatMsg.getReceiverEmail(), "/queue/messages", chatMsg);
    }

    @GetMapping("/history")
    public List<Message> getHistory(@RequestParam Long userA, @RequestParam Long userB) {
        return messageRepository.findConversation(userA, userB, Sort.by("createdAt").ascending());
    }
}
