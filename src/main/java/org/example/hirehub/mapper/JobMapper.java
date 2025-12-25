package org.example.hirehub.mapper;

import org.example.hirehub.dto.job.CreateJobRequestDTO;
import org.example.hirehub.dto.job.UpdateJobRequestDTO;
import org.example.hirehub.dto.resume.ResumeSummaryDTO;
import org.example.hirehub.dto.user.CompanySummaryDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface JobMapper {

//    @Mapping(target = "is_banned", source = "is_banned")
//    @Mapping(target = "resumes", ignore = true)
//    @Mapping(target = "candidatesCount", ignore = true)
    JobDetailDTO toDTO(Job job);

    CompanySummaryDTO toDTO(User user);

    ResumeSummaryDTO toDTO(Resume resume);

    @Mapping(target = "id", source = "skill.id")
    @Mapping(target = "name", source = "skill.name")
    SkillSummaryDTO toDTO(JobSkill skill);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateJobFromDTO(@MappingTarget Job job, UpdateJobRequestDTO updateJobRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void createJobFromDTO(@MappingTarget Job job, CreateJobRequestDTO createJobRequestDTO);

}
