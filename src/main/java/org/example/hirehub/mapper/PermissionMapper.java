package org.example.hirehub.mapper;

import org.example.hirehub.dto.permission.PermissionDetailDTO;
import org.example.hirehub.dto.role.RoleSummaryDTO;
import org.example.hirehub.entity.Permission;
import org.example.hirehub.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionDetailDTO toDTO(Permission permission);
    RoleSummaryDTO toDTO(Role role);
}
