package org.example.hirehub.service;

import org.example.hirehub.dto.conversation.GroupEventDTO;
import org.example.hirehub.dto.message.MessageDetailDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;
import org.example.hirehub.entity.Conversation;
import org.example.hirehub.entity.ConversationParticipant;
import org.example.hirehub.entity.Message;
import org.example.hirehub.entity.User;
import org.example.hirehub.mapper.MessageMapper;
import org.example.hirehub.repository.ConversationParticipantRepository;
import org.example.hirehub.repository.ConversationRepository;
import org.example.hirehub.repository.MessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service để xử lý các sự kiện group (kick, leave, invite)
 * - Tạo tin nhắn hệ thống
 * - Gửi socket notification cho các participants
 */
@Service
public class GroupEventService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;

    public GroupEventService(
            SimpMessagingTemplate messagingTemplate,
            MessageRepository messageRepository,
            MessageMapper messageMapper,
            ConversationRepository conversationRepository,
            ConversationParticipantRepository participantRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * Xử lý khi một thành viên bị kick khỏi group
     */
    public void handleMemberKicked(Long conversationId, User leader, User kickedUser) {
        String systemMessage = leader.getName() + " đã xóa " + kickedUser.getName() + " khỏi nhóm";

        // Tạo tin nhắn hệ thống (leader là người thực hiện)
        Message message = createSystemMessage(conversationId, systemMessage, leader);

        // Tạo DTO để gửi socket
        GroupEventDTO eventDTO = new GroupEventDTO();
        eventDTO.setConversationId(conversationId);
        eventDTO.setEventType(GroupEventDTO.EventType.MEMBER_KICKED);
        eventDTO.setActor(toUserSummaryDTO(leader));
        eventDTO.setAffectedUsers(List.of(toUserSummaryDTO(kickedUser)));
        eventDTO.setSystemMessage(systemMessage);

        // Gửi socket cho tất cả participants (bao gồm cả người bị kick để họ biết)
        sendGroupEventToAllParticipants(conversationId, eventDTO, message, kickedUser);
    }

    /**
     * Xử lý khi một thành viên tự rời khỏi group
     */
    public void handleMemberLeft(Long conversationId, User leftUser) {
        String systemMessage = leftUser.getName() + " đã rời khỏi nhóm";

        // Tạo tin nhắn hệ thống (người rời là người thực hiện)
        Message message = createSystemMessage(conversationId, systemMessage, leftUser);

        // Tạo DTO để gửi socket
        GroupEventDTO eventDTO = new GroupEventDTO();
        eventDTO.setConversationId(conversationId);
        eventDTO.setEventType(GroupEventDTO.EventType.MEMBER_LEFT);
        eventDTO.setActor(toUserSummaryDTO(leftUser));
        eventDTO.setAffectedUsers(List.of(toUserSummaryDTO(leftUser)));
        eventDTO.setSystemMessage(systemMessage);

        // Gửi socket cho tất cả participants (bao gồm cả người rời để họ biết)
        sendGroupEventToAllParticipants(conversationId, eventDTO, message, leftUser);
    }

    /**
     * Xử lý khi có thành viên mới được mời vào group
     */
    public void handleMembersInvited(Long conversationId, User leader, List<User> invitedUsers) {
        String invitedNames = invitedUsers.stream()
                .map(User::getName)
                .collect(Collectors.joining(", "));

        String systemMessage = leader.getName() + " đã thêm " + invitedNames + " vào nhóm";

        // Tạo tin nhắn hệ thống (leader là người thực hiện)
        Message message = createSystemMessage(conversationId, systemMessage, leader);

        // Tạo DTO để gửi socket
        GroupEventDTO eventDTO = new GroupEventDTO();
        eventDTO.setConversationId(conversationId);
        eventDTO.setEventType(GroupEventDTO.EventType.MEMBER_INVITED);
        eventDTO.setActor(toUserSummaryDTO(leader));
        eventDTO.setAffectedUsers(invitedUsers.stream()
                .map(this::toUserSummaryDTO)
                .collect(Collectors.toList()));
        eventDTO.setSystemMessage(systemMessage);

        // Gửi socket cho tất cả participants (bao gồm cả người mới được mời)
        sendGroupEventToAllParticipants(conversationId, eventDTO, message, invitedUsers);
    }

    /**
     * Xử lý khi một group mới được tạo
     * - Tạo tin nhắn hệ thống: "X đã tạo nhóm và mời Y, Z, ..."
     * - Gửi socket notification cho tất cả participants
     */
    public void handleGroupCreated(Long conversationId, User creator, List<User> invitedUsers) {
        String invitedNames = invitedUsers.stream()
                .filter(u -> !u.getId().equals(creator.getId())) // Loại bỏ creator khỏi danh sách
                .map(User::getName)
                .collect(Collectors.joining(", "));

        String systemMessage;
        if (invitedNames.isEmpty()) {
            systemMessage = creator.getName() + " đã tạo nhóm";
        } else {
            systemMessage = creator.getName() + " đã tạo nhóm và mời " + invitedNames;
        }

        // Tạo tin nhắn hệ thống (creator là người thực hiện)
        Message message = createSystemMessage(conversationId, systemMessage, creator);

        // Tạo DTO để gửi socket
        GroupEventDTO eventDTO = new GroupEventDTO();
        eventDTO.setConversationId(conversationId);
        eventDTO.setEventType(GroupEventDTO.EventType.GROUP_CREATED);
        eventDTO.setActor(toUserSummaryDTO(creator));
        eventDTO.setAffectedUsers(invitedUsers.stream()
                .map(this::toUserSummaryDTO)
                .collect(Collectors.toList()));
        eventDTO.setSystemMessage(systemMessage);

        // Gửi socket cho tất cả participants
        sendGroupEventToAllParticipants(conversationId, eventDTO, message, invitedUsers);
    }

    /**
     * Xử lý khi nhóm bị giải tán bởi leader
     * - Gửi socket notification cho tất cả participants
     * - KHÔNG tạo tin nhắn hệ thống vì conversation sẽ bị xóa
     */
    public void handleGroupDisbanded(Long conversationId, User leader, List<User> allParticipants, String groupName) {
        String systemMessage = leader.getName() + " đã giải tán nhóm \"" + groupName + "\"";

        // Tạo DTO để gửi socket
        GroupEventDTO eventDTO = new GroupEventDTO();
        eventDTO.setConversationId(conversationId);
        eventDTO.setEventType(GroupEventDTO.EventType.GROUP_DISBANDED);
        eventDTO.setActor(toUserSummaryDTO(leader));
        eventDTO.setAffectedUsers(allParticipants.stream()
                .map(this::toUserSummaryDTO)
                .collect(Collectors.toList()));
        eventDTO.setSystemMessage(systemMessage);

        // Gửi socket cho tất cả participants (chỉ gửi event, không tạo message vì
        // conversation sẽ bị xóa)
        for (User participant : allParticipants) {
            messagingTemplate.convertAndSendToUser(
                    participant.getEmail(),
                    "/queue/group-event",
                    eventDTO);
        }
    }

    /**
     * Tạo tin nhắn hệ thống (system message)
     * 
     * @param actor Người thực hiện hành động (dùng làm sender vì DB yêu cầu not
     *              null)
     */
    private Message createSystemMessage(Long conversationId, String content, User actor) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        Message message = new Message();
        message.setConversation(conversation);
        message.setContent(content);
        message.setType("system"); // Type đặc biệt cho system message
        message.setSender(actor); // Dùng actor làm sender (DB không cho phép null)
        message.setCreatedAt(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);

        // Update conversation updatedAt
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return savedMessage;
    }

    /**
     * Gửi group event tới tất cả participants qua socket
     */
    private void sendGroupEventToAllParticipants(
            Long conversationId,
            GroupEventDTO eventDTO,
            Message systemMessage,
            User extraUser // User bị kick hoặc rời (cần gửi thêm cho họ)
    ) {
        sendGroupEventToAllParticipants(conversationId, eventDTO, systemMessage,
                extraUser != null ? List.of(extraUser) : List.of());
    }

    /**
     * Gửi group event tới tất cả participants qua socket (hỗ trợ nhiều extra users)
     */
    private void sendGroupEventToAllParticipants(
            Long conversationId,
            GroupEventDTO eventDTO,
            Message systemMessage,
            List<User> extraUsers // Users được mời mới hoặc bị kick/rời (cần gửi thêm cho họ)
    ) {
        MessageDetailDTO messageDTO = messageMapper.toDTO(systemMessage);

        // Lấy tất cả active participants
        List<ConversationParticipant> participants = participantRepository.findAllByConversationId(conversationId);

        // Tập hợp email đã gửi để tránh gửi trùng
        java.util.Set<String> sentEmails = new java.util.HashSet<>();

        // Gửi cho tất cả active participants
        for (ConversationParticipant participant : participants) {
            if (!participant.isDeleted()) {
                String email = participant.getUser().getEmail();
                sentEmails.add(email);

                // Gửi group event notification
                messagingTemplate.convertAndSendToUser(
                        email,
                        "/queue/group-event",
                        eventDTO);

                // Gửi system message như một message bình thường
                messagingTemplate.convertAndSendToUser(
                        email,
                        "/queue/messages",
                        messageDTO);
            }
        }

        // Gửi thêm cho các users extra (người mới được mời hoặc bị kick/rời)
        if (extraUsers != null) {
            for (User extraUser : extraUsers) {
                String email = extraUser.getEmail();
                // Chỉ gửi nếu chưa gửi
                if (!sentEmails.contains(email)) {
                    messagingTemplate.convertAndSendToUser(
                            email,
                            "/queue/group-event",
                            eventDTO);
                }
            }
        }
    }

    /**
     * Convert User entity sang UserSummaryDTO
     */
    private UserSummaryDTO toUserSummaryDTO(User user) {
        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAvatar(user.getAvatar());
        return dto;
    }
}
