package org.example.hirehub.dto.job;

import org.example.hirehub.dto.user.CompanySummaryDTO;
import org.example.hirehub.dto.resume.ResumeDetailDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

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