package org.example.hirehub.mapper;

import org.example.hirehub.dto.conversation.ConversationDetailDTO;
import org.example.hirehub.dto.message.MessageDetailDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;
import org.example.hirehub.entity.Conversation;
import org.example.hirehub.entity.ConversationParticipant;
import org.example.hirehub.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.MapperConfig;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {MessageMapper.class})
public interface ConversationMapper {

    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapParticipants")
    @Mapping(target = "lastMessage", source = ".", qualifiedByName = "mapLastMessage")
    @Mapping(target = "leaderId", source = "participants", qualifiedByName = "mapLeaderId")
    @Mapping(target = "unreadCount", ignore = true)
    ConversationDetailDTO toDTO(Conversation conversation);

    @Named("mapParticipants")
    default List<UserSummaryDTO> mapParticipants(List<ConversationParticipant> participants) {
        if (participants == null) return List.of();
        return participants.stream()
                .filter(p -> !p.isDeleted())
                .map(p -> {
                    UserSummaryDTO dto = new UserSummaryDTO();
                    dto.setId(p.getUser().getId());
                    dto.setEmail(p.getUser().getEmail());
                    dto.setName(p.getUser().getName());
                    dto.setAvatar(p.getUser().getAvatar());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Named("mapLeaderId")
    default Long mapLeaderId(List<ConversationParticipant> participants) {
        if (participants == null) return null;
        return participants.stream()
                .filter(p -> !p.isDeleted() && p.isLeader())
                .map(p -> p.getUser().getId())
                .findFirst()
                .orElse(null);
    }

    @Named("mapLastMessage")
    default MessageDetailDTO mapLastMessage(Conversation conversation) {
        if (conversation.getMessages() == null || conversation.getMessages().isEmpty()) {
            return null;
        }
        Message lastMessage = conversation.getMessages().stream()
                .filter(m -> !m.isDeleted())
                .max((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
                .orElse(null);
        
        if (lastMessage == null) return null;
        
        // Use MessageMapper through Spring injection
        MessageMapper messageMapper = org.mapstruct.factory.Mappers.getMapper(MessageMapper.class);
        return messageMapper.toDTO(lastMessage);
    }
}

