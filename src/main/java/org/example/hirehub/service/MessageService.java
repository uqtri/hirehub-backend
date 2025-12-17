package org.example.hirehub.service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.example.hirehub.dto.message.CreateMessageDTO;
import org.example.hirehub.entity.Conversation;
import org.example.hirehub.entity.Message;
import org.example.hirehub.entity.User;
import org.example.hirehub.entity.UserMessage;
import org.example.hirehub.exception.UserHandlerException;
import org.example.hirehub.key.UserMessageKey;
import org.example.hirehub.repository.ConversationRepository;
import org.example.hirehub.repository.MessageRepository;
import org.example.hirehub.repository.UserMessageRepository;
import org.example.hirehub.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Setter
@Getter
@Service
public class MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final UserMessageRepository userMessageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationService conversationService;

    public MessageService(
            UserRepository userRepository, 
            MessageRepository messageRepository, 
            UserMessageRepository userMessageRepository,
            ConversationRepository conversationRepository,
            ConversationService conversationService
    ){
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.userMessageRepository = userMessageRepository;
        this.conversationRepository = conversationRepository;
        this.conversationService = conversationService;
    }
    
    @Transactional
    public Message createMessage(CreateMessageDTO dto) throws IOException {
        User sender = userRepository.findByEmail(dto.getSenderEmail());
        if (sender == null) {
            throw new RuntimeException("Sender not found: " + dto.getSenderEmail());
        }

        Conversation conversation = conversationRepository.findById(dto.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + dto.getConversationId()));

        // Verify sender is a participant
        conversationService.getConversationById(dto.getConversationId(), sender.getId());

        Message newMsg = new Message();
        newMsg.setSender(sender);
        newMsg.setConversation(conversation);
        newMsg.setType(dto.getType());
        newMsg.setCreatedAt(LocalDateTime.now());
        newMsg.setContent(dto.getContent());
        newMsg.setFileName(dto.getFileName());
        
        Message savedMessage = messageRepository.save(newMsg);
        
        // Update conversation updatedAt
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        return savedMessage;
    }

    public List<Message> getHistory(Long conversationId, Long userId, List<String> messageTypes) {
        // Verify user is a participant
        conversationService.getConversationById(conversationId, userId);
        
        if (messageTypes == null || messageTypes.isEmpty()) {
            return messageRepository.findByConversationId(conversationId, Sort.by("createdAt").ascending());
        } else {
            return messageRepository.findByConversationId(conversationId, messageTypes, Sort.by("createdAt").ascending());
        }
    }

    public void markSeen(Long userId, Long messageId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));

        // Verify user is a participant of the conversation
        conversationService.getConversationById(message.getConversation().getId(), userId);

        UserMessageKey key = new UserMessageKey(userId, messageId);
        Optional<UserMessage> existing = userMessageRepository.findById(key);
        
        if (existing.isEmpty()) {
            UserMessage newRecord = new UserMessage(user, message);
            newRecord.setCreatedAt(LocalDateTime.now());
            userMessageRepository.save(newRecord);
        }
        
        // Update last read time
        conversationService.updateLastReadAt(message.getConversation().getId(), userId);
    }

    public void reactMessage(Long userId, Long messageId, String emoji) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));

        // Verify user is a participant of the conversation
        conversationService.getConversationById(message.getConversation().getId(), userId);

        UserMessageKey key = new UserMessageKey(userId, messageId);
        Optional<UserMessage> existing = userMessageRepository.findById(key);

        if (existing.isEmpty()) {
            UserMessage newReaction = new UserMessage(user, message);
            newReaction.setEmoji(emoji);
            userMessageRepository.save(newReaction);
            return;
        }

        UserMessage userMsg = existing.get();

        if (userMsg.getEmoji() != null && userMsg.getEmoji().equals(emoji)) {
            userMessageRepository.delete(userMsg);
        } else {
            userMsg.setEmoji(emoji);
            userMessageRepository.save(userMsg);
        }
    }

    public Message getMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
    }
}
