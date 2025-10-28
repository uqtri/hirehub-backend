package org.example.hirehub.dto.languageLevel;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.language.LanguageDetailDTO;
import org.example.hirehub.entity.Language;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LanguageLevelDetailDTO {
    private Long id;
    private String level;
    private LanguageDetailDTO language;
}
