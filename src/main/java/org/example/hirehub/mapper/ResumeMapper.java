package org.example.hirehub.mapper;

import org.example.hirehub.dto.resume.CreateResumeRequestDTO;
import org.example.hirehub.dto.resume.ResumeDetailDTO;
import org.example.hirehub.dto.resume.UpdateResumeRequestDTO;
import org.example.hirehub.dto.user.CompanySummaryDTO;
import org.example.hirehub.dto.job.JobSummaryDTO;
import org.example.hirehub.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    ResumeDetailDTO toDTO(Resume resume);
    CompanySummaryDTO toDTO(User user);
    JobSummaryDTO toDTO(Job job);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void createResumeFromDTO(@MappingTarget Resume resume, CreateResumeRequestDTO createResumeRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateResumeFromDTO(@MappingTarget Resume resume, UpdateResumeRequestDTO updateResumeRequestDTO);

}
