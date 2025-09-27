package org.example.hirehub.mapper;

import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.entity.Skill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillSummaryDTO toDTO(Skill skill);
}
