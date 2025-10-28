package org.example.hirehub.mapper;

import org.example.hirehub.dto.language.LanguageDetailDTO;
import org.example.hirehub.entity.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface LanguageMapper {
    LanguageDetailDTO toDTO (Language language);

}
