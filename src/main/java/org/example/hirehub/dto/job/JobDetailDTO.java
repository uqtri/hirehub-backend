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
    private boolean is_banned;
    private LocalDateTime postingDate;
    private String workspace;
    private String type;
    private String address;

    private List<SkillSummaryDTO> skills;
    private CompanySummaryDTO recruiter;
    private List<ResumeDetailDTO> resumes;
    private Integer hit_counter;
    private boolean isDeleted;
    private String status;
    private Integer candidatesCount;

    // AI violation check results
    private String violationType;
    private String violationExplanation;

    // Admin ban reason
    private String banReason;
}