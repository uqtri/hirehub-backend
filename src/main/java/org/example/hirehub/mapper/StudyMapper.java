package org.example.hirehub.mapper;

import org.example.hirehub.dto.study.CreateStudyRequestDTO;
import org.example.hirehub.dto.study.StudyDetailDTO;
import org.example.hirehub.dto.study.StudySummaryDTO;
import org.example.hirehub.dto.study.UpdateStudyRequestDTO;
import org.example.hirehub.entity.Study;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")

public interface StudyMapper {

    StudyDetailDTO toDTO(Study study);

    Study toEntity (StudyDetailDTO dto);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public void updateFromDTO(@MappingTarget Study study, CreateStudyRequestDTO request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public void updateFromDTO(@MappingTarget Study study, UpdateStudyRequestDTO request);

}
