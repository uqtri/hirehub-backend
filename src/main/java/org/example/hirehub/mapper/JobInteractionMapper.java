package org.example.hirehub.mapper;

import org.example.hirehub.dto.jobInteraction.CreateJobInteractionRequestDTO;
import org.example.hirehub.dto.jobInteraction.JobInteractionDetailDTO;
import org.example.hirehub.entity.JobInteraction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface JobInteractionMapper {

    JobInteractionDetailDTO toDTO(JobInteraction jobInteraction);
    void updateFromDTO(@MappingTarget JobInteraction jobInteraction, CreateJobInteractionRequestDTO request);

}
