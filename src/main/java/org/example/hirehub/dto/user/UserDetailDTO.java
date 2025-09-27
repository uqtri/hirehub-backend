package org.example.hirehub.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.experience.ExperienceSummaryDTO;
import org.example.hirehub.dto.role.RoleDetailDTO;
import org.example.hirehub.dto.role.RoleSummaryDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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