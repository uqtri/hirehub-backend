package org.example.hirehub.service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.example.hirehub.dto.message.CreateMessageDTO;
import org.example.hirehub.entity.Message;
import org.example.hirehub.entity.User;
import org.example.hirehub.entity.UserMessage;
import org.example.hirehub.exception.UserHandlerException;
import org.example.hirehub.key.UserMessageKey;
import org.example.hirehub.repository.MessageRepository;
import org.example.hirehub.repository.UserMessageRepository;
import org.example.hirehub.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
@Service
public class MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final UserMessageRepository userMessageRepository;

    public MessageService(UserRepository userRepository, MessageRepository messageRepository, UserMessageRepository userMessageRepository){
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.userMessageRepository = userMessageRepository;

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

    public List<Message> getHistory(Long userA, Long userB) {
        return messageRepository.findConversation(userA, userB, Sort.by("createdAt").ascending());
    }

    public List<Message> getChatList(Long userId) {
        List<Object[]> pairs = messageRepository.getLatestTimestamps(userId);
        List<Message> result = new ArrayList<>();

        for (Object[] row : pairs) {
            Long userA = ((Number) row[0]).longValue();
            Long userB = ((Number) row[1]).longValue();

            Message msg = messageRepository.getLatestMessageBetween(userA, userB);
            result.add(msg);
        }

        return result;
    }

    public void markSeen(Long userId, Long messageId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));

        UserMessage newRecord = new UserMessage(user, message);
        newRecord.setCreatedAt(LocalDateTime.now());

        userMessageRepository.save(newRecord);
    }

    public void reactMessage(Long userId, Long messageId, String emoji) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));

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

}
