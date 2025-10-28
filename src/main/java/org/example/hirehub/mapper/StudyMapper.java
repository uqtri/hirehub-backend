package org.example.hirehub.mapper;

import org.example.hirehub.dto.study.CreateStudyRequestDTO;
import org.example.hirehub.dto.study.StudyDetailDTO;
import org.example.hirehub.dto.study.StudySummaryDTO;
import org.example.hirehub.dto.study.UpdateStudyRequestDTO;
import org.example.hirehub.entity.Study;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface StudyMapper {

    StudyDetailDTO toDTO(Study study);

    Study toEntity (StudyDetailDTO dto);


    public void updateFromDTO(@MappingTarget Study study, CreateStudyRequestDTO request);
    public void updateFromDTO(@MappingTarget Study study, UpdateStudyRequestDTO request);

}
