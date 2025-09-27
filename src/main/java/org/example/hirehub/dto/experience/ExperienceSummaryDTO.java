package org.example.hirehub.dto.experience;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.user.UserSummaryDTO;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceSummaryDTO {
    private Long id;
    private String position;
    private UserSummaryDTO company;
}