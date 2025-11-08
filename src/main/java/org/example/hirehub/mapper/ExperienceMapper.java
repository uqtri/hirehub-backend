package org.example.hirehub.mapper;

import org.example.hirehub.dto.experience.CreateExperienceForm;
import org.example.hirehub.dto.experience.ExperienceDetailDTO;
import org.example.hirehub.dto.experience.UpdateExperienceForm;
import org.example.hirehub.entity.Experience;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExperienceMapper {

    @Mapping(target = "image", ignore = true)
    Experience toEntity(CreateExperienceForm form);
    ExperienceDetailDTO toDTO(Experience experience);

    @Mapping(target = "image", ignore = true)
    void updateFromForm(@MappingTarget Experience experience, CreateExperienceForm form);

    @Mapping(target = "image", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromForm(@MappingTarget Experience experience, UpdateExperienceForm form);


}
