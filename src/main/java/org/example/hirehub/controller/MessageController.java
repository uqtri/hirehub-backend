package org.example.hirehub.controller;

import jakarta.annotation.Nullable;
import jakarta.mail.Multipart;
import org.example.hirehub.dto.message.CreateMessageDTO;
import org.example.hirehub.dto.message.MessageDetailDTO;

import org.example.hirehub.dto.message.ReactMessageDTO;
import org.example.hirehub.dto.message.SeenMessageDTO;
import org.example.hirehub.entity.Conversation;
import org.example.hirehub.entity.ConversationParticipant;
import org.example.hirehub.entity.Message;
import org.example.hirehub.entity.User;
import org.example.hirehub.mapper.MessageMapper;
import org.example.hirehub.repository.ConversationParticipantRepository;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")

@Controller
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    private final ConversationParticipantRepository participantRepository;

    public MessageController(
            SimpMessagingTemplate messagingTemplate, 
            MessageRepository messageRepository, 
            MessageService messageService, 
            MessageMapper messageMapper, 
            UserService userService, 
            CloudinaryService cloudinaryService,
            ConversationParticipantRepository participantRepository
    ) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.messageMapper = messageMapper;
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
        this.participantRepository = participantRepository;
    }

    @MessageMapping("/chat/message") //client send to /app/chat/message
    public void processMessage(@Payload CreateMessageDTO chatMsg) throws IOException {
        Message createdMessage = messageService.createMessage(chatMsg);
        MessageDetailDTO messageDTO = messageMapper.toDTO(createdMessage);
        
        Conversation conversation = createdMessage.getConversation();
        List<ConversationParticipant> participants = participantRepository.findAllByConversationId(conversation.getId());
        
        // Send to all participants in the conversation
        for (ConversationParticipant participant : participants) {
            if (!participant.isDeleted()) {
                messagingTemplate.convertAndSendToUser(
                    participant.getUser().getEmail(),
                    "/queue/messages", 
                    messageDTO
                );
            }
        }
    }

    @GetMapping("/history")
    public List<MessageDetailDTO> getHistory(
            @RequestParam Long conversationId,
            @RequestParam Long userId,
            @Nullable @RequestParam List<String> messageTypes
    ) {
        return messageService.getHistory(conversationId, userId, messageTypes)
                .stream()
                .map(messageMapper::toDTO)
                .toList();
    }

    @MessageMapping("/message/seen")
    public void markSeen(@Payload SeenMessageDTO msg) {
        messageService.markSeen(msg.getUserId(), msg.getMessageId());

        Message message = messageService.getMessageById(msg.getMessageId());
        Conversation conversation = message.getConversation();
        List<ConversationParticipant> participants = participantRepository.findAllByConversationId(conversation.getId());

        // Notify all participants
        for (ConversationParticipant participant : participants) {
            if (!participant.isDeleted()) {
                messagingTemplate.convertAndSendToUser(
                    participant.getUser().getEmail(), 
                    "/queue/message-seen", 
                    msg
                );
            }
        }
    }

    @MessageMapping("/message/react")
    public void reactEmoji(@Payload ReactMessageDTO msg) {
        messageService.reactMessage(msg.getUserId(), msg.getMessageId(), msg.getEmoji());

        Message message = messageService.getMessageById(msg.getMessageId());
        Conversation conversation = message.getConversation();
        List<ConversationParticipant> participants = participantRepository.findAllByConversationId(conversation.getId());

        // Notify all participants
        for (ConversationParticipant participant : participants) {
            if (!participant.isDeleted()) {
                messagingTemplate.convertAndSendToUser(
                    participant.getUser().getEmail(), 
                    "/queue/message-react", 
                    msg
                );
            }
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = cloudinaryService.uploadAndGetUrl(file, Map.of());
        return ResponseEntity.ok(Map.of("url", url));
    }
}
