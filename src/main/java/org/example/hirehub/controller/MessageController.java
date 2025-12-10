package org.example.hirehub.controller;

import jakarta.mail.Multipart;
import org.example.hirehub.dto.message.CreateMessageDTO;
import org.example.hirehub.dto.message.MessageDetailDTO;

import org.example.hirehub.dto.message.ReactMessageDTO;
import org.example.hirehub.dto.message.SeenMessageDTO;
import org.example.hirehub.entity.Message;
import org.example.hirehub.entity.User;
import org.example.hirehub.mapper.MessageMapper;
import org.example.hirehub.repository.MessageRepository;
import org.example.hirehub.service.CloudinaryService;
import org.example.hirehub.service.MessageService;
import org.example.hirehub.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")

@Controller
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    public MessageController(SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository, MessageService messageService, MessageMapper messageMapper, UserService userService, CloudinaryService cloudinaryService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.messageMapper = messageMapper;
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;

    }

    @MessageMapping("/chat/private") //client send to /app/chat/private
    public void processPrivateMessage(@Payload CreateMessageDTO chatMsg) throws IOException {
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

    @MessageMapping("/message/seen")
    public void markSeen(
            @Payload SeenMessageDTO msg
            ) {
        messageService.markSeen(msg.getUserId(), msg.getMessageId());

        Message message = messageService.getMessageById(msg.getMessageId());
        User sender = message.getSender();
        User receiver = message.getReceiver();
        messagingTemplate.convertAndSendToUser(sender.getEmail(), "/queue/message-seen", msg);
        messagingTemplate.convertAndSendToUser(receiver.getEmail(), "/queue/message-seen", msg);

    }

    @MessageMapping("/message/react")
    public void reactEmoji(
            @Payload ReactMessageDTO msg
    ) {
        messageService.reactMessage(msg.getUserId(), msg.getMessageId(), msg.getEmoji());

        Message message = messageService.getMessageById(msg.getMessageId());
        User sender = message.getSender();
        User receiver = message.getReceiver();

        messagingTemplate.convertAndSendToUser(sender.getEmail(), "/queue/message-react", msg);
        messagingTemplate.convertAndSendToUser(receiver.getEmail(), "/queue/message-react", msg);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = cloudinaryService.uploadAndGetUrl(file, Map.of());
        return ResponseEntity.ok(Map.of("url", url));
    }

}
