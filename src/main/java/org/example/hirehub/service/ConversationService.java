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
    private GroupEventService groupEventService;

    public ConversationService(
            ConversationRepository conversationRepository,
            ConversationParticipantRepository participantRepository,
            UserRepository userRepository,
            MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    // Setter injection để tránh circular dependency
    @org.springframework.beans.factory.annotation.Autowired
    public void setGroupEventService(GroupEventService groupEventService) {
        this.groupEventService = groupEventService;
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
                    dto.getParticipantIds().get(1));

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

        // Nếu là GROUP, gửi system message và socket notification
        if (type == Conversation.ConversationType.GROUP && groupEventService != null) {
            User creator = userRepository.findById(dto.getCreatorId())
                    .orElseThrow(() -> new RuntimeException("Creator not found"));

            List<User> allParticipants = participants.stream()
                    .map(ConversationParticipant::getUser)
                    .collect(Collectors.toList());

            groupEventService.handleGroupCreated(conversation.getId(), creator, allParticipants);
        }

        return conversation;
    }

    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findByUserId(userId);
    }

    public Conversation getConversationById(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        Optional<ConversationParticipant> participant = participantRepository.findByConversationIdAndUserId(
                conversationId, userId);

        if (participant.isEmpty() || participant.get().isDeleted()) {
            throw new RuntimeException("User is not a participant of this conversation");
        }

        return conversation;
    }

    /**
     * Mời user vào group (chỉ leader mới có quyền)
     * - Nếu user chưa từng vào: tạo mới participant với deletedAt = now
     * - Nếu user đã từng rời (leavedAt != null): set leavedAt = null, deletedAt =
     * now
     */
    @Transactional
    public Conversation addParticipants(Long conversationId, AddParticipantsDTO dto, Long userId) {
        Conversation conversation = getConversationById(conversationId, userId);

        if (conversation.getType() == Conversation.ConversationType.DIRECT) {
            throw new RuntimeException("Cannot add participants to a direct conversation");
        }

        // Kiểm tra xem userId có phải là leader không
        ConversationParticipant currentUser = participantRepository
                .findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a participant"));

        if (!currentUser.isLeader()) {
            throw new RuntimeException("Only group leader can invite members");
        }

        User leader = currentUser.getUser();
        List<ConversationParticipant> newParticipants = new ArrayList<>();
        List<User> invitedUsers = new ArrayList<>();

        for (Long participantId : dto.getParticipantIds()) {
            // Kiểm tra xem user đã từng là participant chưa (bao gồm cả những người đã rời)
            Optional<ConversationParticipant> existing = participantRepository
                    .findByConversationIdAndUserIdIncludeLeaved(
                            conversationId, participantId);

            if (existing.isPresent()) {
                ConversationParticipant existingParticipant = existing.get();
                if (existingParticipant.getLeavedAt() != null) {
                    // User đã rời, cho phép mời lại
                    existingParticipant.setLeavedAt(null);
                    existingParticipant.setDeletedAt(LocalDateTime.now()); // Chỉ thấy tin nhắn từ bây giờ
                    participantRepository.save(existingParticipant);
                    invitedUsers.add(existingParticipant.getUser());
                }
                // Nếu user vẫn còn trong group (leavedAt = null), không làm gì
            } else {
                // User chưa từng vào, tạo mới
                User user = userRepository.findById(participantId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + participantId));

                ConversationParticipant participant = new ConversationParticipant(conversation, user);
                // Constructor đã set deletedAt = now()
                newParticipants.add(participant);
                invitedUsers.add(user);
            }
        }

        if (!newParticipants.isEmpty()) {
            participantRepository.saveAll(newParticipants);
        }
        conversation.setUpdatedAt(LocalDateTime.now());
        Conversation savedConversation = conversationRepository.save(conversation);

        // Gửi socket notification và tạo system message
        if (!invitedUsers.isEmpty() && groupEventService != null) {
            groupEventService.handleMembersInvited(conversationId, leader, invitedUsers);
        }

        return savedConversation;
    }

    /**
     * Kick user ra khỏi group (chỉ leader mới có quyền, không thể kick chính mình)
     */
    @Transactional
    public void kickParticipant(Long conversationId, Long participantId, Long userId) {
        Conversation conversation = getConversationById(conversationId, userId);

        if (conversation.getType() == Conversation.ConversationType.DIRECT) {
            throw new RuntimeException("Cannot kick participants from a direct conversation");
        }

        // Kiểm tra xem userId có phải là leader không
        ConversationParticipant currentUser = participantRepository
                .findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a participant"));

        if (!currentUser.isLeader()) {
            throw new RuntimeException("Only group leader can kick members");
        }

        // Không thể kick chính mình
        if (participantId.equals(userId)) {
            throw new RuntimeException("Leader cannot kick themselves");
        }

        ConversationParticipant participant = participantRepository.findByConversationIdAndUserId(
                conversationId, participantId).orElseThrow(() -> new RuntimeException("Participant not found"));

        User leader = currentUser.getUser();
        User kickedUser = participant.getUser();

        LocalDateTime now = LocalDateTime.now();
        participant.setLeavedAt(now);
        participant.setDeletedAt(now);
        participantRepository.save(participant);

        conversation.setUpdatedAt(now);
        conversationRepository.save(conversation);

        // Gửi socket notification và tạo system message
        if (groupEventService != null) {
            groupEventService.handleMemberKicked(conversationId, leader, kickedUser);
        }
    }

    /**
     * User tự rời khỏi group (leader không thể rời nếu còn thành viên khác)
     */
    @Transactional
    public void leaveGroup(Long conversationId, Long userId) {
        Conversation conversation = getConversationById(conversationId, userId);

        if (conversation.getType() == Conversation.ConversationType.DIRECT) {
            throw new RuntimeException("Cannot leave a direct conversation");
        }

        ConversationParticipant participant = participantRepository.findByConversationIdAndUserId(
                conversationId, userId).orElseThrow(() -> new RuntimeException("Participant not found"));

        // Nếu là leader và còn thành viên khác thì không thể rời
        if (participant.isLeader()) {
            List<ConversationParticipant> activeParticipants = participantRepository
                    .findAllByConversationId(conversationId);
            if (activeParticipants.size() > 1) {
                throw new RuntimeException(
                        "Leader cannot leave the group while there are other members. Please transfer leadership first or kick all members.");
            }
        }

        User leftUser = participant.getUser();

        LocalDateTime now = LocalDateTime.now();
        participant.setLeavedAt(now);
        participant.setDeletedAt(now);
        participantRepository.save(participant);

        conversation.setUpdatedAt(now);
        conversationRepository.save(conversation);

        // Gửi socket notification và tạo system message
        if (groupEventService != null) {
            groupEventService.handleMemberLeft(conversationId, leftUser);
        }
    }

    /**
     * Giải tán nhóm (chỉ leader mới có quyền)
     * - Soft delete tất cả participants
     * - Gửi socket notification cho tất cả thành viên
     */
    @Transactional
    public void disbandGroup(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        if (conversation.getType() == Conversation.ConversationType.DIRECT) {
            throw new RuntimeException("Cannot disband a direct conversation");
        }

        // Kiểm tra xem userId có phải là leader không
        ConversationParticipant currentUser = participantRepository
                .findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a participant"));

        if (!currentUser.isLeader()) {
            throw new RuntimeException("Only group leader can disband the group");
        }

        User leader = currentUser.getUser();
        String groupName = conversation.getName();

        // Lấy tất cả participants còn active
        List<ConversationParticipant> participants = participantRepository.findAllByConversationId(conversationId);
        List<User> allUsers = participants.stream()
                .filter(p -> !p.isDeleted())
                .map(ConversationParticipant::getUser)
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();

        // Soft delete tất cả participants
        for (ConversationParticipant participant : participants) {
            if (!participant.isDeleted()) {
                participant.setLeavedAt(now);
                participant.setDeletedAt(now);
            }
        }
        participantRepository.saveAll(participants);

        // Đánh dấu conversation đã bị xóa
        conversation.setUpdatedAt(now);
        conversationRepository.save(conversation);

        // Gửi socket notification
        if (groupEventService != null) {
            groupEventService.handleGroupDisbanded(conversationId, leader, allUsers, groupName);
        }
    }

    @Transactional
    @Deprecated
    public void removeParticipant(Long conversationId, Long participantId, Long userId) {
        // Giữ lại cho tương thích ngược, nhưng nên dùng kickParticipant
        kickParticipant(conversationId, participantId, userId);
    }

    @Transactional
    public void updateLastReadAt(Long conversationId, Long userId) {
        ConversationParticipant participant = participantRepository.findByConversationIdAndUserId(
                conversationId, userId).orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.setLastReadAt(LocalDateTime.now());
        participantRepository.save(participant);
    }

    public Message getLastMessage(Long conversationId) {
        List<Message> messages = messageRepository.findLatestByConversationId(
                conversationId,
                Sort.by("createdAt").descending());
        return messages.isEmpty() ? null : messages.get(0);
    }

    public Long getUnreadCount(Long conversationId, Long userId) {
        ConversationParticipant participant = participantRepository.findByConversationIdAndUserId(
                conversationId, userId).orElseThrow(() -> new RuntimeException("Participant not found"));

        LocalDateTime lastReadAt = participant.getLastReadAt();
        LocalDateTime deletedAt = participant.getDeletedAt();
        List<Message> allMessages = messageRepository.findByConversationId(conversationId,
                Sort.by("createdAt").ascending());

        // Lọc tin nhắn sau deletedAt
        List<Message> visibleMessages = allMessages.stream()
                .filter(m -> deletedAt == null || m.getCreatedAt().isAfter(deletedAt))
                .toList();

        if (lastReadAt == null) {
            return (long) visibleMessages.size();
        }

        long readCount = visibleMessages.stream()
                .filter(m -> m.getCreatedAt().isBefore(lastReadAt) || m.getCreatedAt().equals(lastReadAt))
                .count();

        return (long) visibleMessages.size() - readCount;
    }

    /**
     * Lấy deletedAt của participant để dùng filter messages
     */
    public LocalDateTime getParticipantDeletedAt(Long conversationId, Long userId) {
        ConversationParticipant participant = participantRepository.findByConversationIdAndUserId(
                conversationId, userId).orElseThrow(() -> new RuntimeException("Participant not found"));

        return participant.getDeletedAt();
    }
}
