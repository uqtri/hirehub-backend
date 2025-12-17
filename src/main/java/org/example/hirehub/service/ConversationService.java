package org.example.hirehub.service;

import jakarta.transaction.Transactional;
import org.example.hirehub.dto.conversation.AddParticipantsDTO;
import org.example.hirehub.dto.conversation.CreateConversationDTO;
import org.example.hirehub.entity.Conversation;
import org.example.hirehub.entity.ConversationParticipant;
import org.example.hirehub.entity.Message;
import org.example.hirehub.entity.User;
import org.example.hirehub.repository.ConversationParticipantRepository;
import org.example.hirehub.repository.ConversationRepository;
import org.example.hirehub.repository.MessageRepository;
import org.example.hirehub.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public ConversationService(
            ConversationRepository conversationRepository,
            ConversationParticipantRepository participantRepository,
            UserRepository userRepository,
            MessageRepository messageRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Conversation createConversation(CreateConversationDTO dto) {
        Conversation.ConversationType type = Conversation.ConversationType.valueOf(dto.getType().toUpperCase());
        
        if (dto.getParticipantIds() == null || dto.getParticipantIds().size() < 2) {
            throw new RuntimeException("Conversation must have at least 2 participants");
        }

        if (!dto.getParticipantIds().contains(dto.getCreatorId())) {
            throw new RuntimeException("Creator must be one of the participants");
        }

        if (type == Conversation.ConversationType.DIRECT) {
            if (dto.getParticipantIds().size() != 2) {
                throw new RuntimeException("Direct conversation must have exactly 2 participants");
            }
            
            Optional<Conversation> existing = conversationRepository.findDirectConversationBetween(
                    dto.getParticipantIds().get(0),
                    dto.getParticipantIds().get(1)
            );
            
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Conversation conversation = new Conversation();
        conversation.setType(type);
        conversation.setName(dto.getName());
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation = conversationRepository.save(conversation);

        // Add participants
        List<ConversationParticipant> participants = new ArrayList<>();
        for (Long userId : dto.getParticipantIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            
            ConversationParticipant participant = new ConversationParticipant(conversation, user);
            if (type == Conversation.ConversationType.GROUP && userId.equals(dto.getCreatorId())) {
                participant.setLeader(true);
            }
            participants.add(participant);
        }
        
        participantRepository.saveAll(participants);
        conversation.setParticipants(participants);

        return conversation;
    }

    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findByUserId(userId);
    }

    public Conversation getConversationById(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        Optional<ConversationParticipant> participant = participantRepository.findByConversationIdAndUserId(
                conversationId, userId
        );
        
        if (participant.isEmpty() || participant.get().isDeleted()) {
            throw new RuntimeException("User is not a participant of this conversation");
        }

        return conversation;
    }

    @Transactional
    public Conversation addParticipants(Long conversationId, AddParticipantsDTO dto, Long userId) {
        Conversation conversation = getConversationById(conversationId, userId);

        if (conversation.getType() == Conversation.ConversationType.DIRECT) {
            throw new RuntimeException("Cannot add participants to a direct conversation");
        }

        List<ConversationParticipant> newParticipants = new ArrayList<>();
        for (Long participantId : dto.getParticipantIds()) {
            Optional<ConversationParticipant> existing = participantRepository.findByConversationIdAndUserId(
                    conversationId, participantId
            );

            if (existing.isEmpty() || existing.get().isDeleted()) {
                User user = userRepository.findById(participantId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + participantId));
                
                ConversationParticipant participant = new ConversationParticipant(conversation, user);
                newParticipants.add(participant);
            }
        }

        participantRepository.saveAll(newParticipants);
        conversation.setUpdatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    @Transactional
    public void removeParticipant(Long conversationId, Long participantId, Long userId) {
        Conversation conversation = getConversationById(conversationId, userId);

        if (conversation.getType() == Conversation.ConversationType.DIRECT) {
            throw new RuntimeException("Cannot remove participants from a direct conversation");
        }

        ConversationParticipant participant = participantRepository.findByConversationIdAndUserId(
                conversationId, participantId
        ).orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.setDeleted(true);
        participantRepository.save(participant);
        
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    @Transactional
    public void updateLastReadAt(Long conversationId, Long userId) {
        ConversationParticipant participant = participantRepository.findByConversationIdAndUserId(
                conversationId, userId
        ).orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.setLastReadAt(LocalDateTime.now());
        participantRepository.save(participant);
    }

    public Message getLastMessage(Long conversationId) {
        List<Message> messages = messageRepository.findLatestByConversationId(
                conversationId, 
                Sort.by("createdAt").descending()
        );
        return messages.isEmpty() ? null : messages.get(0);
    }

    public Long getUnreadCount(Long conversationId, Long userId) {
        ConversationParticipant participant = participantRepository.findByConversationIdAndUserId(
                conversationId, userId
        ).orElseThrow(() -> new RuntimeException("Participant not found"));

        LocalDateTime lastReadAt = participant.getLastReadAt();
        List<Message> allMessages = messageRepository.findByConversationId(conversationId, Sort.by("createdAt").ascending());
        
        if (lastReadAt == null) {
            return (long) allMessages.size();
        }

        long readCount = allMessages.stream()
                .filter(m -> m.getCreatedAt().isBefore(lastReadAt) || m.getCreatedAt().equals(lastReadAt))
                .count();
        
        return (long) allMessages.size() - readCount;
    }
}

