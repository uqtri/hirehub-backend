package org.example.hirehub.dto.language;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class LanguageDetailDTO {
    private Long id;
    private String name;
    private String shortName;
}
