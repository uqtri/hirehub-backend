package org.example.hirehub.entity;

import jakarta.persistence.*;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.example.hirehub.key.RolePermissionKey;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Setter

public class RolePermission {

    @EmbeddedId
    private RolePermissionKey rolePermissionKey;

    @ManyToOne
    @MapsId("roleId")
    private Role role;
    @ManyToOne
    @MapsId("permissionId")
    private Permission permission;

    private LocalDateTime createdAt = LocalDateTime.now();

    public RolePermission(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
        this.rolePermissionKey = new RolePermissionKey(role.getId(), permission.getId());
    }


}
