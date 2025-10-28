package org.example.hirehub.mapper;

import org.example.hirehub.dto.language.LanguageDetailDTO;
import org.example.hirehub.dto.languageLevel.LanguageLevelDetailDTO;
import org.example.hirehub.dto.permission.PermissionSummaryDTO;
import org.example.hirehub.dto.study.StudySummaryDTO;
import org.example.hirehub.dto.user.CreateUserRequestDTO;
import org.example.hirehub.dto.user.UpdateUserRequestDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.dto.role.RoleDetailDTO;
import org.example.hirehub.dto.user.UserDetailDTO;
import org.example.hirehub.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "skills", source = "userSkills")
    UserDetailDTO toDTO(User user);

    @Mapping(target = "permissions", source = "rolePermission")
    RoleDetailDTO toDTO(Role role);

    PermissionSummaryDTO toDTO(Permission permission);

    @Mapping(target="id", source= "permission.id")
    @Mapping(target = "action", source = "permission.action")
    @Mapping(target="resource", source="permission.resource")
    PermissionSummaryDTO toDTO(RolePermission rolePermission);

    @Mapping(target = "id", source = "skill.id")
    @Mapping(target = "name", source = "skill.name")
    SkillSummaryDTO toDTO(UserSkill userSkill);

    User toEntity(CreateUserRequestDTO createUserRequestDTO);

    SkillSummaryDTO toDTO(Skill skill);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDTO(@MappingTarget User user, CreateUserRequestDTO createUserRequestDTO);

    @Mapping(target = "avatar", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDTO(@MappingTarget User user, UpdateUserRequestDTO updateUserRequestDTO);


    LanguageLevelDetailDTO toDTO (LanguageLevel languageLevel);

    LanguageDetailDTO toDTO(Language language);

    StudySummaryDTO toDTO (Study study);
}
