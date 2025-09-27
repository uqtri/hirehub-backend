package org.example.hirehub.mapper;

import org.example.hirehub.dto.permission.PermissionSummaryDTO;
import org.example.hirehub.dto.role.RoleDetailDTO;
import org.example.hirehub.entity.Permission;
import org.example.hirehub.entity.Role;
import org.example.hirehub.entity.RolePermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", source = "rolePermission")
    RoleDetailDTO toDTO(Role role);

    @Mapping(target = "id", source = "permission.id")
    @Mapping(target = "action", source = "permission.action")
    @Mapping(target = "resource", source = "permission.resource")


    PermissionSummaryDTO toDTO (RolePermission rolePermission);
    PermissionSummaryDTO toDTO(Permission permission);
}

