package org.example.hirehub.controller;

import org.example.hirehub.dto.message.CreateMessageDTO;
import org.example.hirehub.dto.message.MessageDetailDTO;
import org.example.hirehub.entity.Message;
import org.example.hirehub.mapper.MessageMapper;
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
    private final MessageService messageService;
    private final MessageMapper messageMapper;

    public MessageController(SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository, MessageService messageService, MessageMapper messageMapper) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @MessageMapping("/chat/private") //client send to /app/chat/private
    public void processPrivateMessage(@Payload CreateMessageDTO chatMsg) {
        messageService.createMessage(chatMsg);

        messagingTemplate.convertAndSendToUser(chatMsg.getSenderEmail(),"/queue/messages", chatMsg);
        messagingTemplate.convertAndSendToUser(chatMsg.getReceiverEmail(), "/queue/messages", chatMsg);
    }

    @GetMapping("/history")
    public List<MessageDetailDTO> getHistory(@RequestParam Long userA, @RequestParam Long userB) {
        return  messageService.getHistory(userA, userB).stream().map(messageMapper::toDTO).toList();
    }

    @GetMapping("/chat-list")
    public List<MessageDetailDTO> getChatList(@RequestParam Long userId) {
        return messageService.getChatList(userId).stream().map(messageMapper::toDTO).toList();
    }
}
