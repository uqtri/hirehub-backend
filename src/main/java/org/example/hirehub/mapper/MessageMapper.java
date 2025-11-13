package org.example.hirehub.mapper;

import org.example.hirehub.dto.language.LanguageDetailDTO;
import org.example.hirehub.dto.message.MessageDetailDTO;
import org.example.hirehub.entity.Language;
import org.example.hirehub.entity.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface MessageMapper {
    MessageDetailDTO toDTO (Message message);

}
