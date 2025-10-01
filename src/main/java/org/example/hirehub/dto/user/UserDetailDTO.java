package org.example.hirehub.dto.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.example.hirehub.dto.experience.ExperienceSummaryDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.dto.role.RoleDetailDTO;

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

    private RoleDetailDTO role;
    private List<SkillSummaryDTO> skills;
    private List<ExperienceSummaryDTO> experiences;
}