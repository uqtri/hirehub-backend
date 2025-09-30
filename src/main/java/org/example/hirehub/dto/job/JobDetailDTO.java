package org.example.hirehub.dto.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.resume.ResumeDetailDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.dto.user.CompanySummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobDetailDTO {
    private Long id;
    private String title;
    private String description;
    private String level;
    private Boolean isBanned;
    private LocalDateTime postingDate;

    private List<SkillSummaryDTO> skills;
    private CompanySummaryDTO recruiter;
    private List<ResumeDetailDTO> resumes;
    private Integer hit_counter;
}