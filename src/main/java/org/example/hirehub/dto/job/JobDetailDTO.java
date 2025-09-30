package org.example.hirehub.dto.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.resume.ResumeDetailDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobDetailDTO {

    private Long id;
    private String description;
    private String level;
    private Boolean isBanned;
    private List<SkillSummaryDTO> skills;
    private UserSummaryDTO recruiter;
    private List<ResumeDetailDTO> resumes;
}