package org.example.hirehub.mapper;

import org.example.hirehub.dto.language.LanguageDetailDTO;
import org.example.hirehub.dto.languageLevel.LanguageLevelDetailDTO;
import org.example.hirehub.entity.Language;
import org.example.hirehub.entity.LanguageLevel;

public interface LanguageLevelMapper {

    LanguageLevelDetailDTO toDTO (LanguageLevel languageLevel);

    LanguageDetailDTO toDTO(Language language);
}
