package org.example.hirehub.mapper;

import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.dto.resume.CreateResumeRequestDTO;
import org.example.hirehub.dto.resume.ResumeDetailDTO;
import org.example.hirehub.dto.resume.UpdateResumeRequestDTO;
import org.example.hirehub.dto.user.CompanySummaryDTO;

import org.example.hirehub.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    @Mapping(source = "cover_letter", target = "coverLetter")
    ResumeDetailDTO toDTO(Resume resume);

    CompanySummaryDTO toDTO(User user);

    JobDetailDTO toDTO(Job job);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void createResumeFromDTO(@MappingTarget Resume resume, CreateResumeRequestDTO createResumeRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateResumeFromDTO(@MappingTarget Resume resume, UpdateResumeRequestDTO updateResumeRequestDTO);

}
