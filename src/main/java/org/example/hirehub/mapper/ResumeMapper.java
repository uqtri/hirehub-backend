package org.example.hirehub.mapper;

import org.example.hirehub.dto.job.CreateJobRequestDTO;
import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.dto.job.JobSummaryDTO;
import org.example.hirehub.dto.job.UpdateJobRequestDTO;
import org.example.hirehub.dto.resume.ResumeDetailDTO;
import org.example.hirehub.dto.resume.ResumeSummaryDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.dto.user.CompanySummaryDTO;
import org.example.hirehub.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    ResumeDetailDTO toDTO(Resume resume);
    CompanySummaryDTO toDTO(User user);
    JobSummaryDTO toDTO(Job job);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void createJobFromDTO(@MappingTarget Job job, CreateJobRequestDTO createJobRequestDTO);

}
