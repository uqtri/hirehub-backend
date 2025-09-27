package org.example.hirehub.dto.experience;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceDetailDTO {
    private Long id;
    private String position;
    private UserSummaryDTO company;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    private List<SkillSummaryDTO> skills; // Experience → Skills
}