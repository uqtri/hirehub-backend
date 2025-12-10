package org.example.hirehub.mapper;

import org.example.hirehub.dto.message.SeenMessageDTO;
import org.example.hirehub.dto.message.SeenUserDTO;
import org.example.hirehub.entity.User;
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
    default List<SeenUserDTO> mapSeenUsers(List<UserMessage> seenBy) {
        if (seenBy == null) return List.of();
        return seenBy.stream().map(um -> {
            SeenUserDTO dto = new SeenUserDTO();
            dto.setId(um.getUser().getId());
            dto.setEmail(um.getUser().getEmail());
            dto.setEmoji(um.getEmoji());

            return dto;
        }).collect(Collectors.toList());
    }

}
