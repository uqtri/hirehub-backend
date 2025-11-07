package org.example.hirehub.dto.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.example.hirehub.dto.experience.ExperienceSummaryDTO;
import org.example.hirehub.dto.languageLevel.LanguageLevelDetailDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.dto.role.RoleDetailDTO;
import org.example.hirehub.dto.study.StudyDetailDTO;
import org.example.hirehub.dto.study.StudySummaryDTO;
import org.example.hirehub.entity.LanguageLevel;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class UserDetailDTO {
    private Long id;
    private String email;
    private String name;
    private String avatar;
    private String address;
    private String description;
    private Boolean isVerified;
    private Boolean isBanned;
    private String numberOfEmployees;
    private Integer foundedYear = 0;
    private String github;
    private String resume_link;
    private String field;
    private String resumeFileName;

    private RoleDetailDTO role;
    private List<SkillSummaryDTO> skills;
    private List<ExperienceSummaryDTO> experiences;
    private List<LanguageLevelDetailDTO> languages;
    private List<StudySummaryDTO> studies;
    private String position;
    private String status;
}