package org.example.hirehub.mapper;

import org.example.hirehub.entity.UserMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.example.hirehub.entity.Message;
import org.example.hirehub.dto.message.MessageDetailDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "seenUsers", source = "seenBy", qualifiedByName = "mapSeenUsers")
    MessageDetailDTO toDTO(Message message);

    @Named("mapSeenUsers")
    default List<UserSummaryDTO> mapSeenUsers(List<UserMessage> seenBy) {
        if (seenBy == null) return List.of();
        return seenBy.stream().map(um -> {
            UserSummaryDTO dto = new UserSummaryDTO();
            dto.setId(um.getUser().getId());
            dto.setEmail(um.getUser().getEmail());
            dto.setName(um.getUser().getName());
            dto.setAddress(um.getUser().getAddress());
            dto.setAvatar(um.getUser().getAvatar());
            dto.setPosition(um.getUser().getPosition());
            dto.setStatus(um.getUser().getStatus());
            dto.setResume_name(um.getUser().getResume_name());
            return dto;
        }).collect(Collectors.toList());
    }

}
