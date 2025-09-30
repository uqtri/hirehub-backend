package org.example.hirehub.mapper;

import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.dto.resume.ResumeSummaryDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.dto.user.CompanySummaryDTO;
import org.example.hirehub.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobMapper {

    JobDetailDTO toDTO(Job job);
    CompanySummaryDTO toDTO(User user);
    ResumeSummaryDTO toDTO(Resume resume);

    @Mapping(target = "id", source = "skill.id")
    @Mapping(target = "name", source = "skill.name")
    SkillSummaryDTO toDTO (JobSkill skill);


}
