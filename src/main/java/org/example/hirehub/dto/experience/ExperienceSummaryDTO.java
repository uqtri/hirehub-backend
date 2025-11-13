package org.example.hirehub.dto.experience;

import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceSummaryDTO {
    private Long id;
    private String position;
    private UserSummaryDTO company;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String image;
    private List<SkillSummaryDTO> skills;
    private String type;
    private boolean isDeleted;

}